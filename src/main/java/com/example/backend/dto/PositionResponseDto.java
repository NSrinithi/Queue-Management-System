package com.example.backend.dto;

public class PositionResponseDto {
    private int position;
    private int timeestimated;

    public PositionResponseDto(){

    }

    public PositionResponseDto(int position, int timeestimated) {
        this.position = position;
        this.timeestimated = timeestimated;
    }

    
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTimeestimated() {
        return timeestimated;
    }

    public void setTimeestimated(int timeestimated) {
        this.timeestimated = timeestimated;
    }
}

