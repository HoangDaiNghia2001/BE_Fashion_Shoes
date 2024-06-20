package com.example.response;

import com.example.Entity.Comment;

public class CommentResponse extends Response{
    private Comment comment;

    public CommentResponse() {
    }

    public CommentResponse(Comment comment) {
        this.comment = comment;
    }

    public CommentResponse(String message, Boolean success, Comment comment) {
        super(message, success);
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
