package com.example.backend.service;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.backend.dto.PositionResponseDto;
import com.example.backend.dto.QueueResponseDto;
import com.example.backend.entity.QueueEntry;
import com.example.backend.entity.Status;
import com.example.backend.repo.QueueRepo;


@Service
public class QueueService {

    private QueueRepo qr;
    private RedisTemplate<String,Object> redis;
    private static final int TIME_PER_PERSON=5;

    private static final String QUEUE_KEY = "queue";

    public QueueService(QueueRepo qr, RedisTemplate<String, Object> redis) {
        this.qr = qr;
        this.redis = redis;
    }

    

    public QueueResponseDto addQueue(String name,boolean isVip){
        QueueEntry queue=new QueueEntry();
        queue.setName(name);
        queue.setStatus(Status.WAITING);
        queue.setIsVip(isVip);
        QueueEntry saved=qr.save(queue);
        saved.setTokenNumber(saved.getId().intValue());
        if(isVip){
            redis.opsForList().leftPush(QUEUE_KEY, saved.getId());
        }
        else{
            redis.opsForList().rightPush(QUEUE_KEY, saved.getId());
        }
        QueueEntry queu=qr.save(saved);
        return new QueueResponseDto(queu.isIsVip(),queu.getName(),queu.getStatus(),queu.getTokenNumber());
    }

    public String testREdis(){
        redis.opsForList().rightPush("queue", "sri");
        redis.opsForList().rightPush("queue", "nithi");
        return (String) redis.opsForList().leftPop("queue");
    }

   
    public List<QueueEntry> getAllQueue(){
        return qr.findAll();
    }

    public QueueEntry callNext(){
        QueueEntry current=qr.findFirstByStatusOrderByIdAsc(Status.SERVING);
        if(current!=null){
            current.setStatus(Status.COMPLETED);
            qr.save(current);
        }
        Object val=redis.opsForList().leftPop(QUEUE_KEY);
        if(val==null) throw new RuntimeException("Queue is empty");
        Long id=Long.parseLong(val.toString());
        QueueEntry next=qr.findById(id).orElse(null);
        if(next==null) return null;
        next.setStatus(Status.SERVING);
        return qr.save(next);
    }

    public QueueEntry current(){
        return qr.findFirstByStatusOrderByIdAsc(Status.SERVING);
    }

    public PositionResponseDto getPosition(Long id){
        List<Object> waitingList=redis.opsForList().range(QUEUE_KEY, 0, -1);
        int position=0;
        if(waitingList==null) return null;
        for(int i=0;i<waitingList.size();i++){
            Long val = Long.parseLong(waitingList.get(i).toString());
            if(val.equals(id)){
                position=i+1;
                break;
            }
        }
        if(position == 0){
            throw new RuntimeException("User not found in queue");
        }
        int time=(position-1)*TIME_PER_PERSON;
        return new PositionResponseDto(position, time);
    }


    public void cancel(Long id){
        Long removed=redis.opsForList().remove(QUEUE_KEY,1, id);
        if(removed==0) throw new RuntimeException("User is not found in queue");
        QueueEntry delete=qr.findById(id).orElse(null);
        if(delete!=null){
            delete.setStatus(Status.COMPLETED);
            qr.save(delete);
        }

    }

    public String clearQueue() {
        redis.delete(QUEUE_KEY);
        return "Queue cleared";
    }

}
