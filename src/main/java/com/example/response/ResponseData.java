package com.example.response;

public class ResponseData <T>{
    private String message;
    private Boolean success = true;
    private int status;
    private T results;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ResponseData() {
    }

    public ResponseData(String message, int status, T results) {
        this.message = message;
        this.success = true;
        this.status = status;
        this.results = results;
    }
}
