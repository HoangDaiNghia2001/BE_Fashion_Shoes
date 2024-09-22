package com.example.service;

import com.example.Entity.CustomUserDetails;
import com.example.Entity.User;
import com.example.exception.CustomException;
import com.example.request.OtpRequest;
import com.example.request.PasswordRequest;
import com.example.request.ResetPasswordRequest;
import com.example.request.UserRequest;
import com.example.response.*;
import jakarta.mail.MessagingException;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;

public interface UserService {
    CustomUserDetails loadUserByUsername(String email) throws CustomException;

    Authentication authenticate(String email, String password) throws CustomException;

    User getById(Long id) throws CustomException;

    UserResponse findUserProfileByJwt(String token) throws CustomException, CustomException;

    User findUserByEmail(String email) throws CustomException;

    UserResponse registerUser(UserRequest user) throws CustomException;

    ListUsersResponse filterUserByAdmin(String code, String email, String province, String district, String ward, boolean inactive, int pageIndex, int pageSize) throws CustomException;

    UserResponse updateInformation(UserRequest user, String token) throws CustomException;

    UserResponse updateInformationUser(UserRequest userRequest) throws CustomException;

    UserResponse updateInformationAdmin(UserRequest adminRequest) throws CustomException, IOException;

    Response changePassword(PasswordRequest passwordRequest, String token) throws CustomException;

    Response changePasswordUser(PasswordRequest passwordRequest) throws CustomException;

    Response changePasswordAdmin(PasswordRequest passwordRequest) throws CustomException;

    Response validateOtp(OtpRequest otpRequest) throws CustomException;

    Response resetPassword(ResetPasswordRequest resetPasswordRequest) throws CustomException;

    Long totalUsers();

    List<TopFiveUsersBoughtTheMostResponse> getTopFiveUsersBoughtTheMost();

    UserResponse createUserByAdmin(UserRequest userRequest) throws CustomException, MessagingException;

    UserResponse updateUserByAdmin(long id, UserRequest userRequest) throws CustomException, MessagingException;

    Response deleteUserByAdmin(long id) throws CustomException, MessagingException;

    Response deleteSomeUsersByAdmin(List<Long> ids) throws MessagingException, CustomException;

    Response deactivateUser(Long id) throws CustomException, MessagingException;

    Response reactivateUser(Long id) throws CustomException, MessagingException;
}
