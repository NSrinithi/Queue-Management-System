package com.example.backend.service;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.backend.entity.QueueEntry;
import com.example.backend.entity.Status;
import com.example.backend.repo.QueueRepo;


@Service
public class QueueService {

    private QueueRepo qr;
    private RedisTemplate<String,Object> redis;

    private static final String QUEUE_KEY = "queue";

    public QueueService(QueueRepo qr, RedisTemplate<String, Object> redis) {
        this.qr = qr;
        this.redis = redis;
    }

    

    public QueueEntry addQueue(String name,boolean isVip){
        QueueEntry queue=new QueueEntry();
        queue.setName(name);
        queue.setStatus(Status.WAITING);
        queue.setIsVip(isVip);
        QueueEntry savedd=qr.save(queue);
        savedd.setTokenNumber(savedd.getId().intValue());
        if(isVip){
            redis.opsForList().leftPush(QUEUE_KEY, savedd.getId());
        }
        else{
            redis.opsForList().rightPush(QUEUE_KEY, savedd.getId());
        }
        
        return qr.save(savedd);
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

    public int getPosition(Long id){
        List<Object> waitingList=redis.opsForList().range(QUEUE_KEY, 0, -1);
        if(waitingList==null) return -1;
        for(int i=0;i<waitingList.size();i++){
            Long val = Long.parseLong(waitingList.get(i).toString());
            if(val.equals(id)){
                return i + 1;
            }
        }
        return -1;
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
