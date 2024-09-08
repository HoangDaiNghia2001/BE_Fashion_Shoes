package com.example.service.implement;

import com.example.constant.CookieConstant;
import com.example.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void sendEmail(String to, String subject, String emailTemplate, Context context) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String htmlContent = templateEngine.process(emailTemplate, context);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }

    @Override
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

    @Override
    public String getEmailCookie(HttpServletRequest request) {
        Cookie emailCookie = WebUtils.getCookie(request, CookieConstant.EMAIL_COOKIE);
        if (emailCookie != null) {
            return emailCookie.getValue();
        }
        return null;
    }

    @Override
    public ResponseCookie cleanEmailCookie() {
        return ResponseCookie.from(CookieConstant.EMAIL_COOKIE, "").path("/").maxAge(0).build();
    }
}
