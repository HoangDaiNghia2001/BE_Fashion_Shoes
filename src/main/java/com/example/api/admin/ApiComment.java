package com.example.api.admin;

import com.example.Entity.Comment;
import com.example.exception.CustomException;
import com.example.response.Response;
import com.example.service.implement.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("commentOfAdmin")
@RequestMapping("/api/admin")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiComment {
    @Autowired
    private CommentServiceImpl commentService;

    @GetMapping("/comment")
    public ResponseEntity<?> getAllComment(@RequestParam("pageIndex")int pageIndex, @RequestParam("pageSize")int pageSize){
        List<Comment> comments = commentService.getAllComment(pageIndex,pageSize);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/commentOfUser")
    public ResponseEntity<?> getAllCommentOfUser(@RequestParam("idUser")Long idUser) throws CustomException {
        List<Comment> comments = commentService.getAllCommentOfUser(idUser);
        return new ResponseEntity<>(comments,HttpStatus.OK);
    }

    @DeleteMapping("/comment")
    public ResponseEntity<?> deleteComment(@RequestParam("id")Long id) throws CustomException {
        String message = commentService.deleteCommentByAdmin(id);

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage(message);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
