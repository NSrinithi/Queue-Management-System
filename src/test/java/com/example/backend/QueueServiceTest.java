package com.example.backend;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.backend.dto.PositionResponseDto;
import com.example.backend.dto.QueueResponseDto;
import com.example.backend.entity.QueueEntry;
import com.example.backend.entity.Status;
import com.example.backend.exception.InvalidInputException;
import com.example.backend.exception.QueueEmptyException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repo.QueueRepo;
import com.example.backend.service.QueueService;

@ExtendWith(MockitoExtension.class)
public class QueueServiceTest {

    @Mock
    private QueueRepo qr;

    @Mock
    private RedisTemplate<String, Object> redis;

    @Mock
    private ListOperations<String, Object> listOps;

    @InjectMocks
    private QueueService qs;

    @BeforeEach
    void setup() {
        lenient().when(redis.opsForList()).thenReturn(listOps);
    }

    @Nested
    class AddQueueTests {

        @Test
        void addQueue_whenNormalUser_shouldUseRightPush() {
            QueueEntry entry = new QueueEntry();
            entry.setIsVip(false);
            entry.setId(1L);
            entry.setName("Sri");
            entry.setStatus(Status.WAITING);

            when(qr.save(any(QueueEntry.class))).thenReturn(entry);
            QueueResponseDto response = qs.addQueue("Sri", false);
            assertAll(
                ()->assertEquals("Sri", response.getName()),
                ()->assertEquals(false, response.isIsVip()),
                ()->assertEquals(Status.WAITING, response.getStatus()),
                ()->assertEquals(1, response.getTokenNumber())
            );
            verify(listOps).rightPush("queue", 1L);
            verify(listOps,never()).leftPush(any(),any());
            verifyNoMoreInteractions(listOps);
            // First save: persist initial entry to get DB-generated ID
            // Second save: update entry with token number after Redis push
            verify(qr, times(2)).save(any(QueueEntry.class));
        }

        @Test
        void addQueue_whenVipUser_shouldUseLeftPush() {
            QueueEntry entry = new QueueEntry();
            entry.setIsVip(true);
            entry.setId(2L);
            entry.setName("Nithi");
            entry.setStatus(Status.WAITING);

            when(qr.save(any(QueueEntry.class))).thenReturn(entry);

            QueueResponseDto response = qs.addQueue("Nithi", true);
            assertAll(
                ()->assertEquals("Nithi", response.getName()),
                ()->assertEquals(true, response.isIsVip()),
                ()->assertEquals(Status.WAITING, response.getStatus()),
                ()->assertEquals(2, response.getTokenNumber())
            );
            verify(listOps).leftPush("queue", 2L);
            
            verify(qr, times(2)).save(any(QueueEntry.class));
        }

        @Test
        void addQueue_whenNullUser_shouldThrowException() {
            assertThrows(InvalidInputException.class, () -> {
                qs.addQueue(null, false);
            });
        }

        @Test
        void addQueue_whenRepoFails_shouldThrowException() {
            when(qr.save(any())).thenThrow(new RuntimeException("DB is down"));
            assertThrows(RuntimeException.class, () -> {
                qs.addQueue("sri", false);
            });
        }

        @Test
        void addQueue_whenRedisFails_shouldThrowException() {
            QueueEntry entry = new QueueEntry();
            entry.setId(3L);
            entry.setIsVip(false);
            entry.setName("indra");
            entry.setStatus(Status.WAITING);
            when(qr.save(any(QueueEntry.class))).thenReturn(entry);
            doThrow(new RuntimeException("Redis down")).when(listOps).rightPush(any(), any());
            assertThrows(RuntimeException.class, () -> {
                qs.addQueue("indra", false);
            });
        }

    }

    @Nested
    class CallNextTests {

        @Test
        void callNext_whenQueueEmpty_shouldThrowException() {
            when(listOps.leftPop("queue")).thenReturn(null);
            assertThrows(QueueEmptyException.class, () -> {
                qs.callNext();

            });
            verify(qr, never()).findById(any());
        }

        @Test
        void callNext_whenUserNotFound_shouldReturnError(){
            when(listOps.leftPop("queue")).thenReturn(1L);
            when(qr.findById(1L)).thenReturn(Optional.empty());
           assertThrows(ResourceNotFoundException.class, ()->{
            qs.callNext();
           });
        }

        @Test
        void callNext_whenUserAlreadyServing_shouldCompletePrevious() {
            QueueEntry current = new QueueEntry();
            current.setId(80L);
            current.setStatus(Status.SERVING);
            current.setName("SRi");
            QueueEntry now = new QueueEntry();
            now.setId(1L);
            now.setStatus(Status.WAITING);
            now.setName("indr");
            when(qr.findFirstByStatusOrderByIdAsc(Status.SERVING)).thenReturn(current);
            when(listOps.leftPop("queue")).thenReturn(1L);
            when(qr.findById(1L)).thenReturn(Optional.of(now));
            when(qr.save(any())).thenReturn(now);
            QueueResponseDto res = qs.callNext();
            assertEquals(Status.SERVING, res.getStatus());
            verify(qr, times(2)).save(any());
            verify(listOps).leftPop("queue");
        }

    }

    @Nested
    class getPositionTests {

        @Test
        void getPosition_whenNormalUser_shouldReturnPosition() {
            when(listOps.range("queue", 0, -1)).thenReturn(List.of(1L, 2L, 3L));
            PositionResponseDto res = qs.getPosition(2L);
            assertAll(
                ()->assertEquals(2, res.getPosition()),
                ()->assertEquals(5, res.getTimeestimated())
            );
            verify(listOps).range("queue", 0, -1);
        }

        @Test
        void getPosition_whenUserOutOfRange_shouldThrowException() {
            when(listOps.range("queue", 0, -1)).thenReturn(List.of(1L, 2L));
            assertThrows(ResourceNotFoundException.class, () -> {
                qs.getPosition(9L);
             });  
            verify(listOps).range("queue", 0, -1); 
        }

        @Test
        void getPosition_whenRedisReturnsNull_shouldReturnNull() {
            when(listOps.range("queue", 0, -1)).thenReturn(null);
            PositionResponseDto res = qs.getPosition(1L);
            assertEquals(null, res);
        }


        @ParameterizedTest
        @CsvSource({
            "1,0",
            "2,5",
            "3,10",
            "4,15"
        })
        void getPosition_shouldCalculateTimeEstimated(int pos,int time){
            List<Object> list=List.of(1L,2L,3L,4L);
            when(listOps.range("queue", 0, -1)).thenReturn(list);
            PositionResponseDto res=qs.getPosition((long)pos);
            assertEquals(time,res.getTimeestimated());
        }

    }

    @Nested
    class cancelTests {
        @Test
        void cancel_whenValidId_shouldMarkCompleted() {
            QueueEntry entry = new QueueEntry();
            entry.setId(1L);
            when(listOps.remove("queue", 1, 1L)).thenReturn(1L);
            when(qr.findById(1L)).thenReturn(Optional.of(entry));
            when(qr.save(any(QueueEntry.class))).thenReturn(entry);
            qs.cancel(1L);
            assertEquals(Status.COMPLETED, entry.getStatus());
            verify(listOps).remove("queue", 1, 1L);
            verify(qr).save(any());
        }

        @Test
        void cancel_whenIdNull_shouldThrowException() {
            assertThrows(InvalidInputException.class, () -> {
                qs.cancel(null);
            });
        }

        @Test
        void cancel_whenRedisReturnNull_shouldReturnNull() {
            when(listOps.remove("queue", 1, 3L)).thenReturn(0L);
            assertThrows(ResourceNotFoundException.class, () -> {
                qs.cancel(3L);
            });
            verify(listOps).remove("queue", 1, 3L);
        }
    }

    @Test
    void addQueue_shouldSaveDBBeforeRedis(){
        QueueEntry entry=new QueueEntry();
        entry.setId(1L);
        when(qr.save(any(QueueEntry.class))).thenReturn(entry);
        qs.addQueue("sri",false);
        InOrder inOrder=inOrder(qr,listOps);
        inOrder.verify(qr).save(any());
        inOrder.verify(listOps).rightPush(any(),any());
    }

    @Test
    void addQueue_shouldsaveCorrectDataToDB(){ 
        ArgumentCaptor<QueueEntry> captor = ArgumentCaptor.forClass(QueueEntry.class);
        QueueEntry entry = new QueueEntry();
        entry.setId(1L);
        when(qr.save(captor.capture())).thenReturn(entry);
        qs.addQueue("sri", false);
        verify(qr, times(2)).save(any());
        List<QueueEntry> res = captor.getAllValues();
        assertAll(
            ()->assertEquals("sri", res.get(0).getName()),
            ()->assertEquals(false, res.get(0).isIsVip()),
            ()->assertEquals(Status.WAITING, res.get(0).getStatus()),
            ()->assertEquals(1, res.get(1).getId())
        );
    }              
                
}