package com.example.backend.controller;


import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.QueueRequestdto;
import com.example.backend.entity.QueueEntry;
import com.example.backend.service.QueueService;




@RestController
@RequestMapping("/queue")
public class QueueController {

    private QueueService qs;

    public QueueController(QueueService qs) {
        this.qs = qs;
    }

    
    
    @PostMapping("/join")
    public QueueEntry  addQueu(@RequestBody QueueRequestdto request) {
        return qs.addQueue(request.getName(),request.isIsVip());
    }

    @GetMapping("/get")
    public List<QueueEntry> getQueue() {
        return qs.getAllQueue();
    }

    @GetMapping("/next")
    public QueueEntry callNext() {
        return qs.callNext();
    }

    @GetMapping("/current")
    public QueueEntry getCurrent() {
        return qs.current();
    }
    
    @GetMapping("/position")
    public int getPosition(@RequestParam Long id) {
        return qs.getPosition(id);
    }

    @DeleteMapping("/cancel")
    public void delete(@RequestParam Long id){
        qs.cancel(id);
    }

    @GetMapping("/testRedius")
    public String getMethodName() {
        return qs.testREdis();
    }

    @GetMapping("/clear")
    public String getMethod() {
        return qs.clearQueue();
    }
    
    
    
    
}
