package com.example.api.user;

import com.example.Entity.Comment;
import com.example.exception.CustomException;
import com.example.request.CommentRequest;
import com.example.response.CommentResponse;
import com.example.response.Response;
import com.example.service.implement.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController("commentOfUser")
@RequestMapping("/api/user")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiComment {
    @Autowired
    private CommentServiceImpl commentService;

    @PostMapping("/comment")
    public ResponseEntity<?> createComment(@RequestPart("comment") CommentRequest commentRequest,
                                           @RequestPart("file") MultipartFile[] multipartFiles,
                                           @RequestParam("idProduct") Long idProduct) throws CustomException, IOException {
        Comment comment = commentService.createComment(idProduct, commentRequest, multipartFiles);

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setComment(comment);
        commentResponse.setMessage("Comment created success !!!");
        commentResponse.setSuccess(true);

        return new ResponseEntity<>(commentResponse, HttpStatus.OK);
    }

    @PutMapping("/comment")
    public ResponseEntity<?> updateComment(@RequestParam("idComment") Long id,
                                           @RequestPart("file")MultipartFile[] multipartFiles,
                                           @RequestPart("comment") CommentRequest commentRequest) throws CustomException, IOException {
        Comment comment = commentService.updateComment(id, commentRequest, multipartFiles);

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setComment(comment);
        commentResponse.setMessage("Comment updated success !!!");
        commentResponse.setSuccess(true);

        return new ResponseEntity<>(commentResponse, HttpStatus.OK);
    }

    @DeleteMapping("/comment")
    public ResponseEntity<?> deleteComment(@RequestParam("id") Long id) throws CustomException {
        String message = commentService.deleteCommentByUser(id);

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage(message);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
