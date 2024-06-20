package com.example.util;

import com.example.constant.CookieConstant;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    public ResponseCookie generateEmailCookie(String email) {
        return ResponseCookie.from(CookieConstant.EMAIL_COOKIE, email)
                .domain(".railway.app")
                .path("/")
                .maxAge(10 * 60)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public String getEmailCookie(HttpServletRequest request){
        Cookie emailCookie = WebUtils.getCookie(request, CookieConstant.EMAIL_COOKIE);
        if(emailCookie != null){
            return emailCookie.getValue();
        }
        return null;
    }

    public ResponseCookie cleanEmailCookie() {
        return ResponseCookie.from(CookieConstant.EMAIL_COOKIE, "").path("/").maxAge(0).build();
    }

    public void sendOtpEmail(String email, String otp) throws MessagingException {
        String message = """
                        <div>
                                      <p style="text-align: left;\s
                                      font-size: 24px;\s
                                      font-weight: bold;\s
                                      color: #c91f28;
                                      padding-bottom: 4px;
                                      border-bottom: 1px solid #dddddd;
                                      letter-spacing: 1.5px;
                                      font-style: italic;
                                      ">
                                          Fashion Shoes
                                      </p>
                                      <p style="color: #3a3a3a;
                                      margin-bottom: 5p;
                                      letter-spacing: 0.5px;">
                                          Hello,
                                      </p>
                                      <p style="color: #3a3a3a;
                                      margin-bottom: 5p;
                                      letter-spacing: 0.5px;">
                                          Please use the verification code below on the website.
                                      </p>
                                      <div style="display: flex;">
                                          <p style="color: #fff;
                                          padding: 6px 40px;
                                          background-color: #c91f28;
                                          font-weight: bold;
                                          display: inline-block;
                                          border-radius: 4px;
                                          margin: auto;
                                          letter-spacing: 1.5px;
                                          font-size: 30px;
                                          ">
                                              %{OTP}
                                          </p>
                                      </div>
                                      <p>Thanks!</p>
                                      <p style="font-style: italic;
                                      font-weight: bold;">Fashion Shoes</p>
                                  </div>
                """;

        message = message.replace("%{OTP}", otp);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("RESET YOUR PASSWORD");
        mimeMessageHelper.setText(message, true);

        javaMailSender.send(mimeMessage);
    }
}
