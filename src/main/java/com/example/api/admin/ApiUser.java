package com.example.api.admin;

import com.example.exception.CustomException;
import com.example.request.UserRequest;
import com.example.response.ListUsersResponse;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.TopFiveUsersBoughtTheMostResponse;
import com.example.service.implement.UserServiceImpl;
import com.example.util.EmailUtil;
import jakarta.mail.MessagingException;
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

    @Autowired
    private EmailUtil emailUtil;

    @GetMapping("/users")
    public ResponseEntity<?> filterUser(@RequestParam(value = "code", required = false) String code,
                                        @RequestParam(value = "email", required = false) String email,
                                        @RequestParam(value = "province", required = false) String province,
                                        @RequestParam(value = "district", required = false) String district,
                                        @RequestParam(value = "ward", required = false) String ward,
                                        @RequestParam("pageIndex") int pageIndex,
                                        @RequestParam("pageSize") int pageSize) {
        ListUsersResponse usersResponse = userService.filterUserByAdmin(code, email, province, district, ward, pageIndex, pageSize);

        ResponseData<ListUsersResponse> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("Get users success !!!");
        responseData.setResults(usersResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUserByAdmin(@RequestParam("id") Long id) throws CustomException, MessagingException {
        Response response = userService.deleteUserByAdmin(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/users/{ids}")
    public ResponseEntity<?> deleteSomeUsersByAdmin(@PathVariable("ids") List<Long> ids) throws CustomException, MessagingException {
        Response response = userService.deleteSomeUsersByAdmin(ids);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUserByAdmin(@RequestBody UserRequest userRequest) throws CustomException, MessagingException {
        userService.createUserByAdmin(userRequest);

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage("Create user success !!!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUserByAdmin(@RequestParam("id") int id,
                                               @RequestBody UserRequest userRequest) throws CustomException, MessagingException {
        userService.updateUserByAdmin(id, userRequest);

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage("Update user success !!!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/total")
    public ResponseEntity<?> totalUsers() {
        Long totalUsers = userService.totalUsers();
        ResponseData<Long> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("Get total Users success !!!");
        responseData.setResults(totalUsers);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/users/top-five/bought-the-most")
    public ResponseEntity<?> getTopFiveUsersBoughtTheMost() {
        List<TopFiveUsersBoughtTheMostResponse> users = userService.getTopFiveUsersBoughtTheMost();
        ResponseData<List<TopFiveUsersBoughtTheMostResponse>> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("Get get top five users bought the most success !!!");
        responseData.setResults(users);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
