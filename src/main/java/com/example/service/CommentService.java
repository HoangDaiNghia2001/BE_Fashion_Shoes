package com.example.service;

import com.example.Entity.Comment;
import com.example.exception.CustomException;
import com.example.request.CommentRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CommentService {
    Comment createComment(Long idProduct, CommentRequest commentRequest, MultipartFile[] multipartFiles) throws CustomException, IOException;
    Comment updateComment(Long id,CommentRequest commentRequest, MultipartFile[] multipartFiles) throws CustomException, IOException;
    String deleteCommentByAdmin(Long id) throws CustomException;

    List<Comment> getAllCommentOfProduct(Long idProduct) throws CustomException;

    List<Comment> getAllCommentOfUser(Long idUser) throws CustomException;

    List<Comment> getAllComment(int pageIndex, int pageSize);

    String deleteCommentByUser(Long id) throws CustomException;
}
