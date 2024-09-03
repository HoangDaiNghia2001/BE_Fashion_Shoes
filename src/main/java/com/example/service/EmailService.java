package com.example.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.thymeleaf.context.Context;

public interface EmailService {
    void sendEmail(String to, String subject, String emailTemplate, Context context) throws MessagingException;
    ResponseCookie generateEmailCookie(String email);
    String getEmailCookie(HttpServletRequest request);
    ResponseCookie cleanEmailCookie();
}
