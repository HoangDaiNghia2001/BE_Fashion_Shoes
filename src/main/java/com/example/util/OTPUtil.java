package com.example.util;

import com.example.constant.CookieConstant;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.Random;

@Component
public class OTPUtil {
    public StringBuilder generateOTP() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        StringBuilder OTP = new StringBuilder(String.valueOf(randomNumber));

        while (OTP.length() < 6) {
            OTP.insert(0, "0");
        }

        return OTP;
    }

    public ResponseCookie generateOtpCookie(String otp) {
        return ResponseCookie.from(CookieConstant.OTP_COOKIE, otp)
                .domain(".railway.app")
                .path("/")
                .maxAge(10 * 60)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public String getOtpFromCookie(HttpServletRequest request) {
        Cookie otpCookie = WebUtils.getCookie(request, CookieConstant.OTP_COOKIE);

        if (otpCookie != null) {
            return otpCookie.getValue();
        }
        return null;
    }

    public ResponseCookie cleanOtpCookie() {
        return ResponseCookie.from(CookieConstant.OTP_COOKIE, "").path("/").maxAge(0).build();
    }
}
