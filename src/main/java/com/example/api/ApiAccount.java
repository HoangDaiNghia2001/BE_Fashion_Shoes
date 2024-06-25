package com.example.api;

import com.example.Entity.CustomUserDetails;
import com.example.Entity.RefreshToken;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.request.*;
import com.example.response.Response;
import com.example.response.ResponseData;
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
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
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

    // CALL SUCCESS
    @PostMapping("/account/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest user) throws CustomException {
        userService.registerUser(user);

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage("Register success !!!");

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

        ResponseCookie token = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_USER,userDetails.getUser());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser().getId());

        ResponseCookie refreshTokenCodeCookie = jwtProvider.generateRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER,refreshToken.getRefreshTokenCode());

        UserResponse userInformation = new UserResponse();
        userInformation.setId(userDetails.getUser().getId());
        userInformation.setCode(userDetails.getUser().getCode());
        userInformation.setAddress(userDetails.getUser().getAddress());
        userInformation.setDistrict(userDetails.getUser().getDistrict());
        userInformation.setProvince(userDetails.getUser().getProvince());
        userInformation.setWard(userDetails.getUser().getWard());
        userInformation.setEmail(userDetails.getUser().getEmail());
        userInformation.setFirstName(userDetails.getUser().getFirstName());
        userInformation.setLastName(userDetails.getUser().getLastName());
        userInformation.setGender(userDetails.getUser().getGender());
        userInformation.setMobile(userDetails.getUser().getMobile());
        userInformation.setCreateAt(userDetails.getUser().getCreatedAt());
        userInformation.setImageBase64(userDetails.getUser().getAvatarBase64());

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setSuccess(true);
        response.setMessage("Login success !!!");
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

        if (check) {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            ResponseCookie token = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_ADMIN,userDetails.getUser());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser().getId());

            ResponseCookie refreshTokenCode = jwtProvider.generateRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_ADMIN,refreshToken.getRefreshTokenCode());

            UserResponse userInformation = new UserResponse();

            userInformation.setId(userDetails.getUser().getId());
            userInformation.setCode(userDetails.getUser().getCode());
            userInformation.setAddress(userDetails.getUser().getAddress());
            userInformation.setDistrict(userDetails.getUser().getDistrict());
            userInformation.setProvince(userDetails.getUser().getProvince());
            userInformation.setWard(userDetails.getUser().getWard());
            userInformation.setEmail(userDetails.getUser().getEmail());
            userInformation.setFirstName(userDetails.getUser().getFirstName());
            userInformation.setLastName(userDetails.getUser().getLastName());
            userInformation.setGender(userDetails.getUser().getGender());
            userInformation.setMobile(userDetails.getUser().getMobile());
            userInformation.setCreateAt(userDetails.getUser().getCreatedAt());
            userInformation.setImageBase64((userDetails.getUser().getAvatarBase64()));

            ResponseData<UserResponse> response = new ResponseData<>();
            response.setSuccess(true);
            response.setMessage("Login success !!!");
            response.setResults(userInformation);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, token.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCode.toString())
                    .body(response);
        }

        Response response = new Response();
        response.setSuccess(false);
        response.setMessage("You not permission to login !!!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PostMapping("/refresh/token/user")
    public ResponseEntity<?> refreshTokenUser(HttpServletRequest request) throws CustomException {
        String refreshTokenCodeCookie = jwtProvider.getRefreshTokenCodeFromCookie(request, CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER);

        if ((refreshTokenCodeCookie != null) && (refreshTokenCodeCookie.length() > 0)) {

            Optional<RefreshToken> refreshToken = refreshTokenService.findByRefreshTokenCode(refreshTokenCodeCookie);

            if (refreshToken.isPresent()) {
                RefreshToken refreshTokenCheck = refreshTokenService.verifyExpiration(refreshToken.get());

                if (refreshTokenCheck != null) {
                    ResponseCookie tokenCookie = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_USER,refreshTokenCheck.getUser());

                    Response response = new Response();
                    response.setMessage("Token is refreshed successfully !!!");
                    response.setSuccess(true);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                            .body(response);
                }
            } else {
                throw new CustomException("Refresh token is not in database !!!");
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

            if (refreshToken.isPresent()) {
                RefreshToken refreshTokenCheck = refreshTokenService.verifyExpiration(refreshToken.get());

                if (refreshTokenCheck != null) {
                    ResponseCookie tokenCookie = jwtProvider.generateTokenCookie(CookieConstant.JWT_COOKIE_ADMIN,refreshTokenCheck.getUser());

                    Response response = new Response();
                    response.setMessage("Token is refreshed successfully !!!");
                    response.setSuccess(true);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                            .body(response);
                }
            } else {
                throw new CustomException("Refresh token is not in database !!!");
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
    public ResponseEntity<?> sendEmailToGetOTP(@RequestBody EmailRequest emailRequest) throws CustomException, MessagingException {
        String otp = userService.sendOTPCode(emailRequest.getEmail());

        ResponseCookie otpCookie = otpUtil.generateOtpCookie(otp);

        ResponseCookie emailCookie = emailUtil.generateEmailCookie(emailRequest.getEmail());

        emailUtil.sendOtpEmail(emailRequest.getEmail(), otp, emailRequest.getEmail());

        Response response = new Response();
        response.setMessage("OTP code has been sent to your email !!!");
        response.setSuccess(true);

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

        ResponseCookie cleanEmailCookie = emailUtil.cleanEmailCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanOtpCookie.toString())
                .header(HttpHeaders.SET_COOKIE, cleanEmailCookie.toString())
                .body(response);
    }
}
