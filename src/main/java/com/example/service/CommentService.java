package com.example.service;

import com.example.Entity.Comment;
import com.example.request.CommentRequest;
import com.example.response.Response;
import com.example.exception.CustomException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentService {
    Comment getById(Long id) throws CustomException;
    Comment createComment(Long idProduct, CommentRequest commentRequest, MultipartFile[] multipartFiles) throws CustomException;
    Comment updateComment(Long id,CommentRequest commentRequest, MultipartFile[] multipartFiles) throws CustomException;
    Response deleteCommentByAdmin(Long id) throws CustomException;

    List<Comment> getAllCommentOfProduct(Long idProduct) throws CustomException;

    List<Comment> getAllCommentOfUser(Long idUser) throws CustomException;

    List<Comment> getAllComment(int pageIndex, int pageSize);

    Response deleteCommentByUser(Long id) throws CustomException;
}
