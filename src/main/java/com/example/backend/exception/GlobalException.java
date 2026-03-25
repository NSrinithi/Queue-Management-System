package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<?> handleInvalids(InvalidInputException i){
        return ResponseEntity.status(404).body(i.getMessage());
    }

    @ExceptionHandler(QueueEmptyException.class)
    public ResponseEntity<?> handleEmpty(QueueEmptyException i){
        return ResponseEntity.status(404).body(i.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException i){
        return ResponseEntity.status(404).body(i.getMessage());
    }
}
