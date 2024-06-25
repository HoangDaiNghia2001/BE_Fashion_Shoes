package com.example.exception;

public class CustomException extends Exception{
    private boolean success;
    public CustomException(String message){
        super(message);
        this.success = false;
    }
}
