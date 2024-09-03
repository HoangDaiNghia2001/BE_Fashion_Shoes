package com.example.api;

import com.example.exception.CustomException;
import com.example.service.implement.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("comment")
@RequestMapping("/api")
public class ApiComment {
    @Autowired
    private CommentServiceImpl commentService;

    @GetMapping("/comment")
    public ResponseEntity<?> getCommentOfProduct(@RequestParam("idProduct") Long idProduct) throws CustomException {
        return new ResponseEntity<>(commentService.getAllCommentOfProduct(idProduct), HttpStatus.OK);
    }
}
