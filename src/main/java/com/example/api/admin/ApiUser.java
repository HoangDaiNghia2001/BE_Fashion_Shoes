package com.example.api.admin;

import com.example.Entity.User;
import com.example.exception.CustomException;
import com.example.response.Response;
import com.example.service.implement.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("admin")
@RequestMapping("/api/admin")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiUser {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/users")
    public ResponseEntity<?> getUser(@RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize")int pageSize){
        List<User> users = userService.getAllUser(pageIndex,pageSize);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestParam("id") Long id) throws CustomException {
        String message = userService.deleteUser(id);
        Response response = new Response();
        response.setMessage(message);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
