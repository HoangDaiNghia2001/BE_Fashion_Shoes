package com.example.util;

import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    public String getEmailFromTokenOfUser() {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);

        return (String) jwtProvider.getClaimsFormToken(token).get("email");
    }

    public String getEmailFromTokenOfAdmin() {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);

        return (String) jwtProvider.getClaimsFormToken(token).get("email");
    }
}
