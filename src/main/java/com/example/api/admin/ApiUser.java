package com.example.api.admin;

import com.example.exception.CustomException;
import com.example.request.UserRequest;
import com.example.response.*;
import com.example.service.implement.UserServiceImpl;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("admin")
@RequestMapping("/api/admin")
public class ApiUser {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/users")
    public ResponseEntity<?> filterUser(@RequestParam(value = "code", required = false) String code,
                                        @RequestParam(value = "email", required = false) String email,
                                        @RequestParam(value = "province", required = false) String province,
                                        @RequestParam(value = "district", required = false) String district,
                                        @RequestParam(value = "ward", required = false) String ward,
                                        @RequestParam(value = "inactive", required = false) boolean inactive,
                                        @RequestParam("pageIndex") int pageIndex,
                                        @RequestParam("pageSize") int pageSize) throws CustomException {
        ListUsersResponse usersResponse = userService.filterUserByAdmin(code, email, province, district, ward, inactive, pageIndex, pageSize);

        ResponseData<ListUsersResponse> responseData = new ResponseData<>();
        responseData.setMessage("Get users success !!!");
        responseData.setResults(usersResponse);
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUserByAdmin(@RequestParam("id") Long id) throws MessagingException, CustomException {
        Response response = userService.deleteUserByAdmin(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/users/{ids}")
    public ResponseEntity<?> deleteSomeUsersByAdmin(@PathVariable("ids") List<Long> ids) throws MessagingException, CustomException {
        Response response = userService.deleteSomeUsersByAdmin(ids);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUserByAdmin(@RequestBody UserRequest userRequest) throws MessagingException, CustomException {
        UserResponse userResponse = userService.createUserByAdmin(userRequest);

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setMessage("Create user success !!!");
        response.setStatus(HttpStatus.CREATED.value());
        response.setResults(userResponse);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUserByAdmin(@RequestParam("id") int id,
                                               @RequestBody UserRequest userRequest) throws MessagingException, CustomException {
        UserResponse userResponse = userService.updateUserByAdmin(id, userRequest);

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setMessage("Update user success !!!");
        response.setStatus(HttpStatus.OK.value());
        response.setResults(userResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/total")
    public ResponseEntity<?> totalUsers() {
        Long totalUsers = userService.totalUsers();
        ResponseData<Long> responseData = new ResponseData<>();
        responseData.setMessage("Get total Users success !!!");
        responseData.setResults(totalUsers);
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/users/top-five/bought-the-most")
    public ResponseEntity<?> getTopFiveUsersBoughtTheMost() {
        List<TopFiveUsersBoughtTheMostResponse> users = userService.getTopFiveUsersBoughtTheMost();
        ResponseData<List<TopFiveUsersBoughtTheMostResponse>> responseData = new ResponseData<>();
        responseData.setMessage("Get get top five users bought the most success !!!");
        responseData.setResults(users);
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PutMapping("/users/deactivate")
    public ResponseEntity<?> deactivateUser(@RequestParam("id") Long id) throws CustomException, MessagingException {
        Response response = userService.deactivateUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/users/reactivate")
    public ResponseEntity<?> reactivateUser(@RequestParam("id") Long id) throws CustomException, MessagingException {
        Response response = userService.reactivateUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
