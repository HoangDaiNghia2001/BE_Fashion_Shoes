package com.example.response;

public class ResponseData <T>{
    private String message;
    private Boolean success;

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

    public ResponseData() {
    }

    public ResponseData(String message, Boolean success, T results) {
        this.message = message;
        this.success = success;
        this.results = results;
    }
}
