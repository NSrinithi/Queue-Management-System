package com.example.backend;


import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.backend.controller.QueueController;
import com.example.backend.dto.PositionResponseDto;
import com.example.backend.dto.QueueResponseDto;
import com.example.backend.entity.Status;
import com.example.backend.exception.QueueEmptyException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.QueueService;

@WebMvcTest(QueueController.class)
public class QueueControllerTest{
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private QueueService qs;


    @Test
    void addQueue_whenValidUser_shouldReturn201() throws Exception{
        QueueResponseDto req=new QueueResponseDto(false, "sri",Status.WAITING,1);
        when(qs.addQueue("sri", false)).thenReturn(req);

        String json="""
            {
               "name":"sri",
               "isVip":false
            }
                """;
        mockMvc.perform(post("/queue/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("sri"))
                .andExpect(jsonPath("$.isVip").value(false))
                .andExpect(jsonPath("$.status").value("WAITING"));
        verify(qs).addQueue(any(), anyBoolean());
    }

    @Test
    void addQueue_whenInvalidUser_shouldReturn400() throws Exception{
        String json="""
            {
               "isVip":false
            }
                """;
        mockMvc.perform(post("/queue/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest()); 
        verify(qs, never()).addQueue(any(), anyBoolean());      
    }

    @Test
    void addQueue_whenServiceDown_shouldThrowError() throws Exception{
        String json="""
            {
               "name":"sri",
               "isVip":false
            }
                """;
        when(qs.addQueue(any(), anyBoolean())).thenThrow(new RuntimeException("Service down"));
        mockMvc.perform(post("/queue/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isInternalServerError());
        verify(qs).addQueue(any(), anyBoolean());
    }


    @Test
    void callNext_whenQueueHasUser_shouldReturn200() throws Exception{
        QueueResponseDto res=new QueueResponseDto(false, "sri", Status.SERVING, 1);
        when(qs.callNext()).thenReturn(res);
        mockMvc.perform(get("/queue/next"))
             .andExpect(jsonPath("$.status").value("SERVING"))
             .andExpect(status().isOk());
        verify(qs).callNext();
    }

    @Test
    void callNext_whenQueueEmpty_shouldReturnError() throws Exception{
        when(qs.callNext()).thenThrow(new QueueEmptyException("queue cannot be empty"));
        mockMvc.perform(get("/queue/next"))
            .andExpect(status().isNotFound());
        verify(qs).callNext();
    }

    @Test
    void callNext_whenServiceDown_shouldReturnError() throws Exception{
        when(qs.callNext()).thenThrow(new RuntimeException("Service down"));
        mockMvc.perform(get("/queue/next"))
                .andExpect(status().isInternalServerError());
        verify(qs).callNext();
        
    }

    @Test
    void current_whenUserIsServing_shouldReturn200() throws Exception{
        QueueResponseDto res=new QueueResponseDto(false, "sri", Status.SERVING, 1);
        when(qs.current()).thenReturn(res);
        mockMvc.perform(get("/queue/current"))
                .andExpect(jsonPath("$.status").value("SERVING"))
                .andExpect(status().isOk());
        }

    @Test
    void getPosition_whenUserHasId_shouldbe200() throws Exception{
        PositionResponseDto res=new PositionResponseDto(2, 5);
        when(qs.getPosition(1L)).thenReturn(res);
        mockMvc.perform(get("/queue/position")
                .param("id","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value(2))
                .andExpect(jsonPath("$.timeestimated").value(5));
    }

    @Test
    void getPosition_whenUserNotFound_shouldReturnError() throws Exception{
        when(qs.getPosition(99L)).thenThrow(new ResourceNotFoundException("User not found"));
        mockMvc.perform(get("/queue/position")
               .param("id", "99"))
               .andExpect(status().isNotFound());
    }

    @Test
    void cancel_whenUserHasId_shouldReturn200() throws Exception{
        doNothing().when(qs).cancel(1L);
        mockMvc.perform(delete("/queue/cancel")
                .param("id","1"))
              .andExpect(status().isNoContent());
        verify(qs).cancel(1L);
    }

    @Test
    void cancel_whenIdMissing_shouldReturn400() throws Exception{
        mockMvc.perform(delete("/queue/cancel"))
              .andExpect(status().isBadRequest());
        verify(qs, never()).cancel(any());
    }
}
