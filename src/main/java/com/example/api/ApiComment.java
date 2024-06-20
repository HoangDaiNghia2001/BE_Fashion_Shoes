package com.example.api;

import com.example.exception.CustomException;
import com.example.service.implement.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("comment")
@RequestMapping("/api")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiComment {
    @Autowired
    private CommentServiceImpl commentService;

    @GetMapping("/comment")
    public ResponseEntity<?> getCommentOfProduct(@RequestParam("idProduct") Long idProduct) throws CustomException {
        return new ResponseEntity<>(commentService.getAllCommentOfProduct(idProduct), HttpStatus.OK);
    }
}
