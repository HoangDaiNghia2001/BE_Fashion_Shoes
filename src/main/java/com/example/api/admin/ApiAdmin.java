package com.example.api.admin;

import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.request.PasswordRequest;
import com.example.request.UserRequest;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.exception.CustomException;
import com.example.response.UserResponse;
import com.example.service.RefreshTokenService;
import com.example.service.implement.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController("authOfAdmin")
@RequestMapping("/api/admin")
public class ApiAdmin {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private HttpServletRequest request;

    // CALL SUCCESS
    @PutMapping(value = "/update/profile")
    public ResponseEntity<?> updateInformation(@RequestBody UserRequest adminRequest) throws CustomException {
        UserResponse adminResponse = userService.updateInformationAdmin(adminRequest);

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setMessage("Updated information success!!!");
        response.setStatus(HttpStatus.OK.value());
        response.setResults(adminResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequest passwordRequest) throws CustomException {
        Response response = userService.changePasswordAdmin(passwordRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PostMapping("/logout")
    public ResponseEntity<?> adminLogout() throws CustomException {
        // delete refresh token in database
        String refreshTokenCode = jwtProvider.getRefreshTokenCodeFromCookie(request, CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_ADMIN);
        refreshTokenService.deleteRefreshToken(refreshTokenCode);

        // delete token and refresh token on cookie
        ResponseCookie cookie = jwtProvider.cleanTokenCookie(CookieConstant.JWT_COOKIE_ADMIN);
        ResponseCookie refreshTokenCookie = jwtProvider.cleanRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_ADMIN);

        Response response = new Response();
        response.setMessage("Logout success !!!");
        response.setStatus(HttpStatus.OK.value());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }
}
