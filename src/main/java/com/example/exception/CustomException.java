package com.example.exception;

import com.example.response.ResponseError;

public class CustomException extends Exception {

    private ResponseError responseError;

    public CustomException(ResponseError responseError) {
        this.responseError = responseError;
    }

    public ResponseError getResponseError() {
        return this.responseError;
    }
}
