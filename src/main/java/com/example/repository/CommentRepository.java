package com.example.repository;

import com.example.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("select c from Comment c where c.productOfComment.id = ?1")
    List<Comment> getAllCommentByProduct(Long idProduct);

    @Query("select c from Comment c where c.userOfComment.id = ?1")
    List<Comment> getAllCommentByUser(Long idUser);
}
