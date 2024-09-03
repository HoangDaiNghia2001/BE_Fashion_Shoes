package com.example.config;

import com.example.Entity.User;
import com.example.constant.CookieConstant;
import com.example.constant.JwtConstant;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JwtConstant.SECRET_KEY));
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 600000L))
                .claim("email", user.getEmail())
                .signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    public ResponseCookie generateTokenCookie(String name, User user) {
        String token = this.generateToken(user);

        return ResponseCookie.from(name, token)
//                .domain(".railway.app")
                .path("/")
                .maxAge(10*60)
//                .maxAge(60*60)
                .httpOnly(true)
                .secure(true)
//                .sameSite("None")600000
                .build();
    }

    public ResponseCookie generateRefreshTokenCodeCookie(String name, String refreshTokenCode) {
        return ResponseCookie.from(name, refreshTokenCode)
//                .domain(".railway.app")
                .path("/")
                .maxAge(24 * 60 * 60 * 10)
                .httpOnly(true)
                .secure(true)
//                .sameSite("None")
                .build();
    }

    public String getTokenFromCookie(HttpServletRequest request, String name) {
        Cookie tokenCookie = WebUtils.getCookie(request, name);

        if (tokenCookie != null) {
            return tokenCookie.getValue();
        }
        return null;
    }

    public String getRefreshTokenCodeFromCookie(HttpServletRequest request, String name) {
        Cookie refreshToken = WebUtils.getCookie(request, name);
        if (refreshToken != null) {
            return refreshToken.getValue();
        }
        return null;
    }

    public ResponseCookie cleanTokenCookie(String name) {
        return ResponseCookie.from(name, "")
//                .domain(".railway.app")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
//                .sameSite("None")
                .build();
    }

    public ResponseCookie cleanRefreshTokenCodeCookie(String name){
        return ResponseCookie.from(name, "")
//                .domain(".railway.app")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
//                .sameSite("None")
                .build();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "JWT token is expired");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty");
        }
    }

    public Claims getClaimsFormToken(String jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("JWT String argument cannot be null or empty !!!");
        }
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
