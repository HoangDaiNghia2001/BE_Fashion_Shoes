package com.example.api;

import com.example.Entity.CustomUserDetails;
import com.example.Entity.RefreshToken;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.request.*;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.UserResponse;
import com.example.service.EmailService;
import com.example.service.RefreshTokenService;
import com.example.service.implement.UserServiceImpl;
import com.example.util.OTPUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@RestController("login")
@RequestMapping("/api")
public class ApiAccount {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private OTPUtil otpUtil;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserMapper userMapper;

    // CALL SUCCESS
    @PostMapping("/account/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest user) throws CustomException {
        UserResponse userResponse = userService.registerUser(user);

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setMessage("Register success !!!");
        response.setStatus(HttpStatus.CREATED.value());
        response.setResults(userResponse);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // CALL SUCCESS
    @PostMapping("/account/user/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) throws CustomException {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = userService.authenticate(email, password);
        // when user log in success
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        ResponseCookie token = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_USER, userDetails.getUser());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser().getId());

        ResponseCookie refreshTokenCodeCookie = jwtProvider.generateRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER, refreshToken.getRefreshTokenCode());

        UserResponse userInformation = userMapper.userToUserResponse(userDetails.getUser());

        ResponseData<UserResponse> response = new ResponseData<>();
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

        Authentication authentication = userService.authenticate(email, password);

        boolean roleAdmin = authentication.getAuthorities().stream().anyMatch(role ->  RoleConstant.ADMIN.equals(role.toString()));

        if (!roleAdmin) {
            throw new CustomException(
                    "You not permission to login !!!",
                    HttpStatus.FORBIDDEN.value()
            );
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        ResponseCookie token = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_ADMIN, userDetails.getUser());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser().getId());

        ResponseCookie refreshTokenCode = jwtProvider.generateRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_ADMIN, refreshToken.getRefreshTokenCode());

        UserResponse adminResponse = userMapper.userToUserResponse(userDetails.getUser());

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setMessage("Login success !!!");
        response.setResults(adminResponse);
        response.setStatus(HttpStatus.OK.value());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, token.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCode.toString())
                .body(response);
    }

    private ResponseEntity<?> refreshToken(HttpServletRequest request, String refreshTokenCode, String tokenName) throws CustomException {
        String refreshTokenCodeCookie = jwtProvider.getRefreshTokenCodeFromCookie(request, refreshTokenCode);

        if ((refreshTokenCodeCookie != null) && (refreshTokenCodeCookie.length() > 0)) {

            RefreshToken refreshToken = refreshTokenService.findByRefreshTokenCode(refreshTokenCodeCookie)
                    .orElseThrow(() -> new CustomException(
                            "Refresh token is not exist !!!",
                            HttpStatus.NOT_FOUND.value()
                    ));

            // check account disable
            if(!refreshToken.getUser().isActive()){
                refreshTokenService.deleteRefreshToken(refreshTokenCodeCookie);
                throw new CustomException(
                        "Your account has been disabled !!!",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
            // check expired
            RefreshToken refreshTokenCheck = refreshTokenService.verifyExpiration(refreshToken);

            if (refreshTokenCheck != null) {
                ResponseCookie tokenCookie = jwtProvider.generateTokenCookie(tokenName, refreshTokenCheck.getUser());

                Response response = new Response();
                response.setMessage("Token is refreshed successfully !!!");
                response.setStatus(HttpStatus.OK.value());

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                        .body(response);
            }
        }

        // xoa nhung token da het han
        refreshTokenService.deleteAllExpiredSince(LocalDateTime.now());

        Response response = new Response();
        response.setMessage("Refresh Token is empty !!!");
        response.setStatus(HttpStatus.OK.value());

        return ResponseEntity.badRequest().body(response);
    }

    // CALL SUCCESS
    @PostMapping("/token/user/refresh")
    public ResponseEntity<?> refreshTokenUser(HttpServletRequest request) throws CustomException {
        return this.refreshToken(request, CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER, CookieConstant.JWT_COOKIE_USER);
    }

    // CALL SUCCESS
    @PostMapping("/token/admin/refresh")
    public ResponseEntity<?> refreshTokenAdmin(HttpServletRequest request) throws CustomException {
        return this.refreshToken(request, CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_ADMIN, CookieConstant.JWT_COOKIE_ADMIN);
    }

    // CALL SUCCESS
    @PostMapping("/forget/password")
    public ResponseEntity<?> sendEmailToGetOTP(@RequestBody EmailRequest emailRequest) throws MessagingException, CustomException {
        User user = userService.findUserByEmail(emailRequest.getEmail());
        String otpCode = String.valueOf(otpUtil.generateOTP());

        ResponseCookie otpCookie = otpUtil.generateOtpCookie(otpCode);

        ResponseCookie emailCookie = emailService.generateEmailCookie(emailRequest.getEmail());

        // send email
        Context context = new Context();
        context.setVariable("otpCode",  otpCode);
        context.setVariable("firstName", user.getFirstName());

        emailService.sendEmail(emailRequest.getEmail(), "Your OTP Code for Verification", "otp_email", context);

        Response response = new Response();
        response.setMessage("OTP code has been sent to your email !!!");
        response.setStatus(HttpStatus.OK.value());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, otpCookie.toString())
                .header(HttpHeaders.SET_COOKIE, emailCookie.toString())
                .body(response);
    }

    // CALL SUCCESS
    @PostMapping("/validate/otp")
    public ResponseEntity<?> validateOTP(@RequestBody OtpRequest otpRequest) throws CustomException {
        return new ResponseEntity<>(userService.validateOtp(otpRequest), HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/reset/password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws CustomException {
        Response response = userService.resetPassword(resetPasswordRequest);

        ResponseCookie cleanOtpCookie = otpUtil.cleanOtpCookie();

        ResponseCookie cleanEmailCookie = emailService.cleanEmailCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanOtpCookie.toString())
                .header(HttpHeaders.SET_COOKIE, cleanEmailCookie.toString())
                .body(response);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestParam("email") String email) throws CustomException {
        User user = userService.findUserByEmail(email);

        ResponseData<User> response = new ResponseData<>();
        response.setMessage("Refresh Token is empty !!!");
        response.setStatus(HttpStatus.OK.value());
        response.setResults(user);

        return ResponseEntity.ok()
                .body(response);
    }
}
