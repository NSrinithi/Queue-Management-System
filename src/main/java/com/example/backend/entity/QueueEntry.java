package com.example.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class QueueEntry {
    
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    
    private int tokenNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean isVip;

    public QueueEntry(){
    }

    public QueueEntry(Long id, boolean isVip, String name, Status status, int tokenNumber) {
        this.id = id;
        this.isVip = isVip;
        this.name = name;
        this.status = status;
        this.tokenNumber = tokenNumber;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isIsVip() {
        return isVip;
    }

    public void setIsVip(boolean isVip) {
        this.isVip = isVip;
    }


}
