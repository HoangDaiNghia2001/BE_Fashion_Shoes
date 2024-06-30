package com.example.service;

import com.example.request.OtpRequest;
import com.example.request.PasswordRequest;
import com.example.request.ResetPasswordRequest;
import com.example.request.UserRequest;
import com.example.response.*;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserResponse findUserById(Long id) throws ResponseError;

    UserResponse findUserProfileByJwt(String token) throws ResponseError, ResponseError;

    UserResponse findUserByEmail(String email) throws ResponseError;

    UserResponse registerUser(UserRequest user) throws ResponseError;

    ListUsersResponse filterUserByAdmin(String code, String email, String province, String district, String ward, int pageIndex, int pageSize) throws ResponseError;

    UserResponse updateInformation(UserRequest user, String token) throws ResponseError;

    UserResponse updateInformationUser(UserRequest userRequest) throws ResponseError;

    UserResponse updateInformationAdmin(UserRequest adminRequest) throws ResponseError, IOException;

    Response changePassword(PasswordRequest passwordRequest, String token) throws ResponseError;

    Response changePasswordUser(PasswordRequest passwordRequest) throws ResponseError;

    Response changePasswordAdmin(PasswordRequest passwordRequest) throws ResponseError;

    String sendOTPCode(String email) throws ResponseError, MessagingException;

    Response validateOtp(OtpRequest otpRequest) throws ResponseError;

    Response resetPassword(ResetPasswordRequest resetPasswordRequest) throws ResponseError;

    Long totalUsers();

    List<TopFiveUsersBoughtTheMostResponse> getTopFiveUsersBoughtTheMost();

    UserResponse createUserByAdmin(UserRequest userRequest) throws ResponseError, MessagingException;

    UserResponse updateUserByAdmin(long id, UserRequest userRequest) throws ResponseError, MessagingException;

    Response deleteUserByAdmin(long id) throws ResponseError, MessagingException;

    Response deleteSomeUsersByAdmin(List<Long> ids) throws MessagingException;

}
