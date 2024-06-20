package com.example.util;

import com.example.Entity.CustomUserDetails;
import com.example.exception.CustomException;
import com.example.service.implement.CustomUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (user == null) {
            throw new CustomException("Invalid username !!!");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("Invalid Password !!!");
        }

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}
