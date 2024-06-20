package com.example.api.user;

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
import java.util.stream.Collectors;

@RestController("user")
@RequestMapping("/api/user")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiUser {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;

    // CALL SUCCESS
    @PutMapping(value = "/update/profile")
    public ResponseEntity<?> updateInformation(@RequestBody UserRequest newUser) throws CustomException, IOException {
        User user = userService.updateInformationUser(newUser);

        UserResponse userInformation = new UserResponse();

        userInformation.setId(user.getId());
        userInformation.setAddress(user.getAddress());
        userInformation.setDistrict(user.getDistrict());
        userInformation.setProvince(user.getProvince());
        userInformation.setWard(user.getWard());
        userInformation.setEmail(user.getEmail());
        userInformation.setFirstName(user.getFirstName());
        userInformation.setLastName(user.getLastName());
        userInformation.setGender(user.getGender());
        userInformation.setMobile(user.getMobile());
        userInformation.setCreateAt(user.getCreatedAt());
        userInformation.setImageBase64(user.getAvatarBase64());

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setMessage("Updated information success!!!");
        response.setSuccess(true);
        response.setResults(userInformation);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PostMapping("/logout")
    public ResponseEntity<?> userLogout() throws CustomException {
        // delete refresh token in database
        String refreshTokenCode = jwtProvider.getRefreshTokenCodeFromCookie(request, CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER);
        refreshTokenService.deleteRefreshTokenByRefreshTokenCode(refreshTokenCode);

        // delete token and refresh token on cookie
        ResponseCookie cookie = jwtProvider.cleanTokenCookie(CookieConstant.JWT_COOKIE_USER);
        ResponseCookie refreshTokenCookie = jwtProvider.cleanRefreshTokenCodeCookie(CookieConstant.JWT_REFRESH_TOKEN_CODE_COOKIE_USER);

        Response response = new Response();
        response.setMessage("Logout success !!!");
        response.setSuccess(true);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    // CALL SUCCESS
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequest passwordRequest) throws CustomException {
        Response response = userService.changePasswordUser(passwordRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/password")
    public ResponseEntity<?> confirmPassword(@RequestBody PasswordRequest passwordRequest) throws CustomException {
        Boolean result = userService.confirmPassword(passwordRequest);
        Response response = new Response();
        response.setSuccess(result);
        response.setMessage(result ? "Password matched !!!" : "Password not match !!!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/information")
    public ResponseEntity<?> getInformation() throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);

        User user = userService.findUserProfileByJwt(token);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
