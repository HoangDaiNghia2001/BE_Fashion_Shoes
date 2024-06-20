package com.example.request;

public class ResetPasswordRequest {
    private String password;
    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
