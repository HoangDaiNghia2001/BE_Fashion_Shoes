package com.example.api.user;

import com.example.Entity.Comment;
import com.example.request.CommentRequest;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.exception.CustomException;
import com.example.service.implement.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("commentOfUser")
@RequestMapping("/api/user")
public class ApiComment {
    @Autowired
    private CommentServiceImpl commentService;

    @PostMapping("/comment")
    public ResponseEntity<?> createComment(@RequestPart("comment") CommentRequest commentRequest,
                                           @RequestPart("file") MultipartFile[] multipartFiles,
                                           @RequestParam("idProduct") Long idProduct) throws CustomException {
        Comment comment = commentService.createComment(idProduct, commentRequest, multipartFiles);

        ResponseData<Comment> responseData = new ResponseData<>();
        responseData.setResults(comment);
        responseData.setMessage("Comment created success !!!");
        responseData.setStatus(HttpStatus.CREATED.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PutMapping("/comment")
    public ResponseEntity<?> updateComment(@RequestParam("idComment") Long id,
                                           @RequestPart("file") MultipartFile[] multipartFiles,
                                           @RequestPart("comment") CommentRequest commentRequest) throws CustomException {
        Comment comment = commentService.updateComment(id, commentRequest, multipartFiles);

        ResponseData<Comment> responseData = new ResponseData<>();
        responseData.setResults(comment);
        responseData.setMessage("Comment updated success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @DeleteMapping("/comment")
    public ResponseEntity<?> deleteComment(@RequestParam("id") Long id) throws CustomException {
        Response response = commentService.deleteCommentByUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
