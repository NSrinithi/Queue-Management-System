package com.example.backend.dto;

import com.example.backend.entity.Status;

public class QueueResponseDto {
    private String name;
    private int tokenNumber;
    private Status status;

    public QueueResponseDto(){

    }
    
    public QueueResponseDto(String name, Status status, int tokenNumber) {
        this.name = name;
        this.status = status;
        this.tokenNumber = tokenNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    




}
