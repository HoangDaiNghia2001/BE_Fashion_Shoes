package com.example.api;

import com.example.Entity.CustomUserDetails;
import com.example.Entity.RefreshToken;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.request.*;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.ResponseError;
import com.example.response.UserResponse;
import com.example.service.RefreshTokenService;
import com.example.service.implement.UserServiceImpl;
import com.example.util.EmailUtil;
import com.example.util.OTPUtil;
import com.example.util.UserUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@RestController("login")
@RequestMapping("/api")
public class ApiAccount {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private OTPUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserMapper userMapper;

    // CALL SUCCESS
    @PostMapping("/account/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest user) throws ResponseError {
        UserResponse userResponse = userService.registerUser(user);

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setSuccess(true);
        response.setMessage("Register success !!!");
        response.setStatus(HttpStatus.CREATED.value());
        response.setResults(userResponse);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // CALL SUCCESS
    @PostMapping("/account/user/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) throws Exception {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = userUtil.authenticate(email, password);
        // when user log in success
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        ResponseCookie token = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_USER, userDetails.getUser());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser().getId());

        ResponseCookie refreshTokenCodeCookie = jwtProvider.generateRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER, refreshToken.getRefreshTokenCode());

        UserResponse userInformation = userMapper.userToUserResponse(userDetails.getUser());

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setSuccess(true);
        response.setMessage("Login success !!!");
        response.setStatus(HttpStatus.OK.value());
        response.setResults(userInformation);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, token.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCodeCookie.toString())
                .body(response);
    }

    // CALL SUCCESS
    @PostMapping("/account/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest loginRequest) throws CustomException {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = userUtil.authenticate(email, password);

        boolean check = false;

        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(RoleConstant.ADMIN)) {
                check = true;
                break;
            }
        }

        if (!check) {
            Response response = new Response();
            response.setSuccess(false);
            response.setMessage("You not permission to login !!!");
            response.setStatus(HttpStatus.FORBIDDEN.value());

            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        ResponseCookie token = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_ADMIN, userDetails.getUser());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser().getId());

        ResponseCookie refreshTokenCode = jwtProvider.generateRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_ADMIN, refreshToken.getRefreshTokenCode());

        UserResponse adminResponse = userMapper.userToUserResponse(userDetails.getUser());

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setSuccess(true);
        response.setMessage("Login success !!!");
        response.setResults(adminResponse);
        response.setStatus(HttpStatus.OK.value());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, token.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCode.toString())
                .body(response);
    }

    // CALL SUCCESS
    @PostMapping("/refresh/token/user")
    public ResponseEntity<?> refreshTokenUser(HttpServletRequest request) throws CustomException {
        String refreshTokenCodeCookie = jwtProvider.getRefreshTokenCodeFromCookie(request, CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER);

        if ((refreshTokenCodeCookie != null) && (refreshTokenCodeCookie.length() > 0)) {

            Optional<RefreshToken> refreshToken = refreshTokenService.findByRefreshTokenCode(refreshTokenCodeCookie);

            if (!refreshToken.isPresent()) {
                ResponseError responseError = new ResponseError();
                responseError.setMessage("Refresh token is not in database !!!");
                responseError.setSuccess(false);
                responseError.setStatus(HttpStatus.NOT_FOUND.value());

                throw new CustomException(responseError);
            }
            RefreshToken refreshTokenCheck = refreshTokenService.verifyExpiration(refreshToken.get());

            if (refreshTokenCheck != null) {
                ResponseCookie tokenCookie = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_USER, refreshTokenCheck.getUser());

                Response response = new Response();
                response.setMessage("Token is refreshed successfully !!!");
                response.setSuccess(true);
                response.setStatus(HttpStatus.OK.value());

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                        .body(response);
            }
        }

        // xoa nhung token da het han
        refreshTokenService.deleteAllExpiredSince(LocalDateTime.now());

        Response response = new Response();
        response.setSuccess(false);
        response.setMessage("Refresh Token is empty !!!");

        return ResponseEntity.badRequest().body(response);
    }

    // CALL SUCCESS
    @PostMapping("/refresh/token/admin")
    public ResponseEntity<?> refreshTokenAdmin(HttpServletRequest request) throws CustomException {
        String refreshTokenCodeCookie = jwtProvider.getRefreshTokenCodeFromCookie(request, CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_ADMIN);

        if ((refreshTokenCodeCookie != null) && (refreshTokenCodeCookie.length() > 0)) {

            Optional<RefreshToken> refreshToken = refreshTokenService.findByRefreshTokenCode(refreshTokenCodeCookie);

            if (!refreshToken.isPresent()) {
                ResponseError responseError = new ResponseError();
                responseError.setMessage("Refresh token is not in database !!!");
                responseError.setSuccess(false);
                responseError.setStatus(HttpStatus.NOT_FOUND.value());

                throw new CustomException(responseError);
            }
            RefreshToken refreshTokenCheck = refreshTokenService.verifyExpiration(refreshToken.get());

            if (refreshTokenCheck != null) {
                ResponseCookie tokenCookie = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_ADMIN, refreshTokenCheck.getUser());

                Response response = new Response();
                response.setMessage("Token is refreshed successfully !!!");
                response.setSuccess(true);
                response.setStatus(HttpStatus.OK.value());
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                        .body(response);
            }
        }

        // xoa nhung token da het han
        refreshTokenService.deleteAllExpiredSince(LocalDateTime.now());

        Response response = new Response();
        response.setSuccess(false);
        response.setMessage("Refresh Token is empty !!!");

        return ResponseEntity.badRequest().body(response);
    }

    // CALL SUCCESS
    @PostMapping("/forget/password")
    public ResponseEntity<?> sendEmailToGetOTP(@RequestBody EmailRequest emailRequest) throws MessagingException, ResponseError {
        String otp = userService.sendOTPCode(emailRequest.getEmail());

        ResponseCookie otpCookie = otpUtil.generateOtpCookie(otp);

        ResponseCookie emailCookie = emailUtil.generateEmailCookie(emailRequest.getEmail());

        emailUtil.sendOtpEmail(emailRequest.getEmail(), otp, emailRequest.getEmail());

        Response response = new Response();
        response.setMessage("OTP code has been sent to your email !!!");
        response.setSuccess(true);
        response.setStatus(HttpStatus.OK.value());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, otpCookie.toString())
                .header(HttpHeaders.SET_COOKIE, emailCookie.toString())
                .body(response);
    }

    // CALL SUCCESS
    @PostMapping("/validate/otp")
    public ResponseEntity<?> validateOTP(@RequestBody OtpRequest otpRequest) throws ResponseError {
        return new ResponseEntity<>(userService.validateOtp(otpRequest), HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/reset/password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws ResponseError {
        Response response = userService.resetPassword(resetPasswordRequest);

        ResponseCookie cleanOtpCookie = otpUtil.cleanOtpCookie();

        ResponseCookie cleanEmailCookie = emailUtil.cleanEmailCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanOtpCookie.toString())
                .header(HttpHeaders.SET_COOKIE, cleanEmailCookie.toString())
                .body(response);
    }
}
