package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class QueueRequestdto {
    
    @NotBlank(message="Name cannot be null or empty")
    private String name;


    private boolean isVip;

    public QueueRequestdto(boolean isVip, String name) {
        this.isVip = isVip;
        this.name = name;
    }

    

    public QueueRequestdto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsVip() {
        return isVip;
    }

    public void setIsVip(boolean isVip) {
        this.isVip = isVip;
    }

    
}
