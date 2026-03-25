package com.example.backend.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.PositionResponseDto;
import com.example.backend.dto.QueueRequestdto;
import com.example.backend.dto.QueueResponseDto;
import com.example.backend.entity.QueueEntry;
import com.example.backend.service.QueueService;

import jakarta.validation.Valid;




@RestController
@RequestMapping("/queue")
public class QueueController {

    private QueueService qs;

    public QueueController(QueueService qs) {
        this.qs = qs;
    }


   
    @PostMapping("/join")
    public ResponseEntity<QueueResponseDto>  addQueue(@Valid @RequestBody QueueRequestdto request) {
        return ResponseEntity.status(201).body(qs.addQueue(request.getName(),request.isIsVip()));
    }


    @GetMapping("/get")
    public ResponseEntity<List<QueueResponseDto>> getQueue() {
        return ResponseEntity.status(200).body(qs.getAllQueue());
    }

    @GetMapping("/next")
    public ResponseEntity<QueueResponseDto> callNext() {
        return ResponseEntity.status(200).body(qs.callNext());
    }

    @GetMapping("/current")
    public ResponseEntity<QueueResponseDto> getCurrent() {
        return ResponseEntity.status(200).body(qs.current());
    }
    
    @GetMapping("/position")
    public ResponseEntity<PositionResponseDto> getPosition(@RequestParam Long id) {
        return ResponseEntity.status(200).body(qs.getPosition(id));
    }

    
    @DeleteMapping("/cancel")
    public ResponseEntity<Void> delete(@RequestParam Long id){
        qs.cancel(id);
        return ResponseEntity.noContent().build();
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
