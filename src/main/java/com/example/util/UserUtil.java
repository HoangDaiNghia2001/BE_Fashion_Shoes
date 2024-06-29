package com.example.util;

import com.example.Entity.CustomUserDetails;
import com.example.exception.CustomException;
import com.example.response.Response;
import com.example.response.ResponseError;
import com.example.service.implement.CustomUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserUtil {
    @Autowired
    private CustomUserServiceImpl customUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Authentication authenticate(String email, String password) throws CustomException {
        CustomUserDetails user = (CustomUserDetails) customUserService.loadUserByUsername(email);
        ResponseError responseError = new ResponseError();
        responseError.setSuccess(false);
        responseError.setStatus(HttpStatus.BAD_REQUEST.value());

        if (user == null) {
            responseError.setMessage("Invalid username !!!");
            throw new CustomException(responseError);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            responseError.setMessage("Invalid Password !!!");
            throw new CustomException(responseError);
        }

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}
