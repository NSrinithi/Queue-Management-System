package com.example.backend.exception;

public class QueueEmptyException extends RuntimeException{
    public QueueEmptyException(String msg){
        super(msg);
    }
}
