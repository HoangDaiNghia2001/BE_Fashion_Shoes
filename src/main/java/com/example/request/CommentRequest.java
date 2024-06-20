package com.example.request;

public class CommentRequest {
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CommentRequest() {
    }

    public CommentRequest(String comment) {
        this.comment = comment;
    }
}
