package com.example.backend.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.QueueEntry;
import com.example.backend.entity.Status;
import java.util.List;


public interface QueueRepo extends JpaRepository<QueueEntry, Long>{
    QueueEntry findFirstByStatusOrderByIdAsc(Status status);

    QueueEntry findByTokenNumber(int token);

    QueueEntry findByName(String name);

    QueueEntry findTopByOrderByTokenNumberDesc();
    
    List<QueueEntry> findByStatusOrderByIdAsc(Status status);
}
