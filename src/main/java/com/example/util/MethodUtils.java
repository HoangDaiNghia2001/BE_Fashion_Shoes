package com.example.util;

import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class MethodUtils {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JwtProvider jwtProvider;

    public static String getBaseURL(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        StringBuffer url =  new StringBuffer();
        url.append(scheme).append("://").append(serverName);
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath);
        if(url.toString().endsWith("/")){
            url.append("/");
        }
        return url.toString();
    }

    public String getEmailFromTokenOfUser() throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        String email = (String) jwtProvider.getClaimsFormToken(token).get("email");

        if(email == null){
            throw new CustomException("Email of user on cookie is null !!!",
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return email;
    }

    public String getEmailFromTokenOfAdmin() throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);

        String email =  (String) jwtProvider.getClaimsFormToken(token).get("email");
        if(email == null){
            throw new CustomException("Email of admin on cookie is null !!!",
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return email;
    }
}
