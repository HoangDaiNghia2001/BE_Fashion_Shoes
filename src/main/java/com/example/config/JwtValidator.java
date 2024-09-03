package com.example.config;

import com.example.Entity.CustomUserDetails;
import com.example.constant.CookieConstant;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtValidator extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tokenCookieAdmin = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
        String tokenCookieUser = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);

        // Lấy URI của request để phân loại
        String requestURI = request.getRequestURI();
        try {
            if (requestURI.startsWith(RoleConstant.ADMIN_URI)) {
                // Xử lý cho admin
                this.setRoleForAuthentication(tokenCookieAdmin, response);

            } else if (requestURI.startsWith(RoleConstant.USER_URI)) {
                // Xử lý cho user
                this.setRoleForAuthentication(tokenCookieUser, response);
            }
        } catch (CustomException e) {
            throw new RuntimeException(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private void setRoleForAuthentication(String tokenCookie, HttpServletResponse response) throws IOException, CustomException {
        if (tokenCookie != null) {
            List<GrantedAuthority> roles = this.getRoleFormTokenCookie(tokenCookie);
            Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, roles);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // Token user không tồn tại hoặc hết hạn
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token not found or expired !!!");
            return;
        }
    }

    private List<GrantedAuthority> getRoleFormTokenCookie(String token) throws CustomException {
        List<GrantedAuthority> roles = new ArrayList<>();
        if (token != null && jwtProvider.validateJwtToken(token)) {
            Claims claims = jwtProvider.getClaimsFormToken(token);

            String email = String.valueOf(claims.get("email"));

            CustomUserDetails user = userService.loadUserByUsername(email);

            roles.addAll(user.getAuthorities());
        }
        return roles;
    }
}
