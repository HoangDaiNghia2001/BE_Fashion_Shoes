package com.example.response;

public class ResponseError {
    private String message;
    private boolean success = false;
    private int status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ResponseError() {
    }

    public ResponseError(String message, int status) {
        this.message = message;
        this.success = false;
        this.status = status;
    }
}
