package com.example.api.admin;

import com.example.Entity.CustomUserDetails;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.request.PasswordRequest;
import com.example.request.UserRequest;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.UserResponse;
import com.example.service.RefreshTokenService;
import com.example.service.implement.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RestController("authOfAdmin")
@RequestMapping("/api/admin")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
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
    public ResponseEntity<?> updateInformation(@RequestBody UserRequest newAdmin) throws CustomException, IOException {
        User user = userService.updateInformationAdmin(newAdmin);

        UserResponse adminInformation = new UserResponse();

        adminInformation.setId(user.getId());
        adminInformation.setAddress(user.getAddress());
        adminInformation.setDistrict(user.getDistrict());
        adminInformation.setProvince(user.getProvince());
        adminInformation.setWard(user.getWard());
        adminInformation.setEmail(user.getEmail());
        adminInformation.setFirstName(user.getFirstName());
        adminInformation.setLastName(user.getLastName());
        adminInformation.setGender(user.getGender());
        adminInformation.setMobile(user.getMobile());
        adminInformation.setCreateAt(user.getCreatedAt());
        adminInformation.setImageBase64(user.getAvatarBase64());

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setMessage("Updated information success!!!");
        response.setSuccess(true);
        response.setResults(adminInformation);

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
        refreshTokenService.deleteRefreshTokenByRefreshTokenCode(refreshTokenCode);

        // delete token and refresh token on cookie
        ResponseCookie cookie = jwtProvider.cleanTokenCookie(CookieConstant.JWT_COOKIE_ADMIN);
        ResponseCookie refreshTokenCookie = jwtProvider.cleanRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_ADMIN);

        Response response = new Response();
        response.setMessage("Logout success !!!");
        response.setSuccess(true);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody UserRequest admin) throws CustomException {
        userService.registerAdmin(admin);

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage("Register admin success !!!");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
