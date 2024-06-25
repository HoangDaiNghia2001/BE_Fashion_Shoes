package com.example.config;

import com.example.Entity.CustomUserDetails;
import com.example.constant.CookieConstant;
import com.example.constant.JwtConstant;
import com.example.exception.CustomException;
import com.example.service.implement.CustomUserServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JwtValidator extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserServiceImpl customUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tokenCookieAdmin = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
        String tokenCookieUser = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);

        List<GrantedAuthority> roles = new ArrayList<>();

        if(tokenCookieAdmin != null){
            roles.addAll(getRoleFormTokenCookie(tokenCookieAdmin));
        }

        if(tokenCookieUser != null){
            roles.addAll(getRoleFormTokenCookie(tokenCookieUser));
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, roles);

        System.out.println(authentication.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    public List<GrantedAuthority> getRoleFormTokenCookie(String token) {
        List<GrantedAuthority> roles = new ArrayList<>();
        if (token != null && jwtProvider.validateJwtToken(token)) {
            try {
                Claims claims = jwtProvider.getClaimsFormToken(token);

                String email = String.valueOf(claims.get("email"));

                CustomUserDetails user = (CustomUserDetails) customUserService.loadUserByUsername(email);

                roles.addAll(user.getAuthorities());
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid token....");
            }
        }
        return roles;
    }
}
