package com.example.request;

import java.util.List;

public class CommentRequest {
    private String comment;
    private List<String> imageComments;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImageComments() {
        return imageComments;
    }

    public void setImageComments(List<String> imageComments) {
        this.imageComments = imageComments;
    }

    public CommentRequest(String comment) {
        this.comment = comment;
    }

    public CommentRequest(String comment, List<String> imageComments) {
        this.comment = comment;
        this.imageComments = imageComments;
    }
}
