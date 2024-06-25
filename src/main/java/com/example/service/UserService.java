package com.example.service;

import com.example.Entity.User;
import com.example.exception.CustomException;
import com.example.request.OtpRequest;
import com.example.request.PasswordRequest;
import com.example.request.ResetPasswordRequest;
import com.example.request.UserRequest;
import com.example.response.ListUsersResponse;
import com.example.response.Response;
import com.example.response.TopFiveUsersBoughtTheMostResponse;
import com.example.response.UserResponse;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User findUserById(Long id) throws CustomException;

    User findUserProfileByJwt(String token) throws CustomException;

    User findUserByEmail(String email) throws CustomException;

    void registerUser(UserRequest user) throws CustomException;

    void registerAdmin(UserRequest admin) throws CustomException;

    ListUsersResponse filterUserByAdmin(String code, String email, String province, String district, String ward, int pageIndex, int pageSize);

    User updateInformation(UserRequest user, String token) throws CustomException, IOException;

    User updateInformationUser(UserRequest userRequest) throws CustomException, IOException;

    User updateInformationAdmin(UserRequest adminRequest) throws CustomException, IOException;

    Boolean confirmPassword(PasswordRequest passwordRequest) throws CustomException;

    Response changePassword(PasswordRequest passwordRequest, String token) throws CustomException;

    Response changePasswordUser(PasswordRequest passwordRequest) throws CustomException;

    Response changePasswordAdmin(PasswordRequest passwordRequest) throws CustomException;

    String sendOTPCode(String email) throws CustomException, MessagingException;

    Response validateOtp(OtpRequest otpRequest) throws CustomException;

    Response resetPassword(ResetPasswordRequest resetPasswordRequest) throws CustomException;

    Long totalUsers();

    List<TopFiveUsersBoughtTheMostResponse> getTopFiveUsersBoughtTheMost();

    void createUserByAdmin(UserRequest userRequest) throws CustomException, MessagingException;
    void updateUserByAdmin(long id, UserRequest userRequest) throws CustomException, MessagingException;
    Response deleteUserByAdmin(long id) throws CustomException, MessagingException;
    Response deleteSomeUsersByAdmin(List<Long> ids) throws MessagingException;

}
