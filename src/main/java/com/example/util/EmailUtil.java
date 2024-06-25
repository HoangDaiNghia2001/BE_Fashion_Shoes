package com.example.util;

import com.example.constant.CookieConstant;
import com.example.response.OrderLineResponse;
import com.example.response.OrderResponse;
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

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Component
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    public ResponseCookie generateEmailCookie(String email) {
        return ResponseCookie.from(CookieConstant.EMAIL_COOKIE, email)
//                .domain(".railway.app")
                .path("/")
                .maxAge(10 * 60)
                .httpOnly(true)
                .secure(true)
//                .sameSite("None")
                .build();
    }

    public String getEmailCookie(HttpServletRequest request) {
        Cookie emailCookie = WebUtils.getCookie(request, CookieConstant.EMAIL_COOKIE);
        if (emailCookie != null) {
            return emailCookie.getValue();
        }
        return null;
    }

    public ResponseCookie cleanEmailCookie() {
        return ResponseCookie.from(CookieConstant.EMAIL_COOKIE, "").path("/").maxAge(0).build();
    }

    public void sendOtpEmail(String email, String otp, String fullName) throws MessagingException {
        String message = """
                        <div>
                                      <p style="text-align: left;\s
                                      font-size: 24px;\s
                                      font-weight: bold;\s
                                      padding-bottom: 4px;
                                      border-bottom: 1px solid #dddddd;
                                      letter-spacing: 1.5px;
                                      ">
                                          Fashion Shoes
                                      </p>
                                      <p style="color: #3a3a3a;
                                      margin-bottom: 5p;
                                      letter-spacing: 0.5px;">
                                          Dear, %{fullName}
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
        message = message.replace("%{fullName}", fullName);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("RESET YOUR PASSWORD");
        mimeMessageHelper.setText(message, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendPassWordEmail(String email, String password, String fullName) throws MessagingException {
        String message = """
                        <div>
                                      <p style="text-align: left;\s
                                      font-size: 24px;\s
                                      font-weight: bold;\s
                                      padding-bottom: 4px;
                                      border-bottom: 1px solid #dddddd;
                                      letter-spacing: 1.5px;
                                      ">
                                          Fashion Shoes
                                      </p>
                                      <p style="color: #3a3a3a;
                                      margin-bottom: 5p;
                                      letter-spacing: 0.5px;">
                                         Dear, %{fullName}
                                      </p>
                                      <p style="color: #3a3a3a;
                                      margin-bottom: 5p;
                                      letter-spacing: 0.5px;">
                                          Here is your password. For security reasons, please change your password after logging in for the first time.
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
                                              %{PASSWORD}
                                          </p>
                                      </div>
                                      <p>Thanks!</p>
                                      <p style="font-style: italic;
                                      font-weight: bold;">Fashion Shoes</p>
                                  </div>
                """;

        message = message.replace("%{PASSWORD}", password);
        message = message.replace("%{fullName}", fullName);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Welcome to Your Service Name - Your New Account Password");
        mimeMessageHelper.setText(message, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendNotificationEmail(String email, String fullName) throws MessagingException {
        String message = """
                        <div>
                                      <p style="text-align: left;\s
                                      font-size: 24px;\s
                                      font-weight: bold;\s
                                      padding-bottom: 4px;
                                      border-bottom: 1px solid #dddddd;
                                      letter-spacing: 1.5px;
                                      ">
                                          Fashion Shoes
                                      </p>
                                      <p style="color: #3a3a3a;
                                      margin-bottom: 5p;
                                      letter-spacing: 0.5px;">
                                      Dear, %{fullName}
                                      </p>
                                      <p style="color: #3a3a3a;
                                      margin-bottom: 5p;
                                      letter-spacing: 0.5px;">
                                          Your personal information has been updated by the store admin. Please review the changes.
                                      </p>
                                      <p>Thanks!</p>
                                      <p style="font-style: italic;
                                      font-weight: bold;">Fashion Shoes</p>
                                  </div>
                """;

        message = message.replace("%{fullName}", fullName);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Your Personal Information Update Notification");
        mimeMessageHelper.setText(message, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendNotificationEmailDeleteUser(String email, String fullName) throws MessagingException {
        String message = """
                        <div>
                                      <p style="text-align: left;\s
                                      font-size: 24px;\s
                                      font-weight: bold;\s
                                      padding-bottom: 4px;
                                      border-bottom: 1px solid #dddddd;
                                      letter-spacing: 1.5px;
                                      ">
                                          Fashion Shoes
                                      </p>
                                      <p style="color: #3a3a3a;
                                      margin-bottom: 5p;
                                      letter-spacing: 0.5px;">
                                      Dear, %{fullName}
                                      </p>
                                      <p style="color: #3a3a3a;
                                      margin-bottom: 5p;
                                      letter-spacing: 0.5px;">
                                          We regret to inform you that your account on Fashion Shoes has been terminated by our admin team due to violations of our Terms of Service.
                                      </p>
                                      <p>Thanks!</p>
                                      <p style="font-style: italic;
                                      font-weight: bold;">Fashion Shoes</p>
                                  </div>
                """;

        message = message.replace("%{fullName}", fullName);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Account Termination Notice Due to Violation of Terms");
        mimeMessageHelper.setText(message, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendOrderEmail(OrderResponse orderResponse) throws MessagingException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        StringBuilder orderSummary = new StringBuilder();

        // Build the order summary table rows
        for (OrderLineResponse item : orderResponse.getOrderLines()) {
            orderSummary.append("<tr>")
                    .append("<td style='text-align: left;'>").append(item.getNameProduct()).append("</td>")
                    .append("<td>").append(item.getSize()).append("</td>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td>").append(decimalFormat.format(item.getTotalPrice())).append("<sup>đ</sup></td>")
                    .append("</tr>");
        }

        String message = """
                <html>
                       <head>
                           <style>
                               body {
                                   font-family: Arial, sans-serif;
                                   line-height: 1.6;
                                   background-color: #f5f5f5;
                                   margin: 0;
                                   padding: 0;
                               }
                               .container {
                                   max-width: 600px;
                                   margin: 20px auto;
                                   background-color: #ffffff;
                                   padding: 20px;
                                   border: 1px solid #dddddd;
                                   border-radius: 5px;
                                   box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                               }
                               .title {
                                   font-size: 24px;
                                   font-weight: bold;
                                   text-align: left;
                                   border-bottom: 1px solid #dddddd;
                                   padding-bottom: 4px;
                                   letter-spacing: 1.5px;
                               }
                               .greeting,
                               .message,
                               .order-info {
                                   color: #3a3a3a;
                                   margin-bottom: 10px;
                                   letter-spacing: 0.5px;
                               }
                               .signature {
                                   font-style: italic;
                                   font-weight: bold;
                                   margin-top: 10px;
                                   color: #3a3a3a;
                               }
                               .order-summary {
                                   width: 100%;
                                   border-collapse: collapse;
                                   margin-top: 20px;
                               }
                               .order-summary th,
                               .order-summary td {
                                   border: 1px solid #dddddd;
                                   padding: 8px;
                                   text-align: center;
                               }
                               .total-label {
                                   font-weight: bold;
                               }
                               .total-amount {
                                   text-align: center;
                               }
                           </style>
                       </head>
                       <body>
                           <div class="container">
                               <p class="title">Fashion Shoes</p>
                               <p class="greeting">Dear, %{fullName}</p>
                               <p class="message">We are writing to inform you that your order has been successfully placed. Please review the
                                   details of your order below:</p>
                               <p>Thank you for shopping with us!</p>
                               <p class="signature">Fashion Shoes</p>
                               <h2>Order Summary</h2>
                               <table class="order-summary">
                                   <thead>
                                       <tr>
                                           <th style="text-align: left;">Product</th>
                                           <th>Size</th>
                                           <th>Quantity</th>
                                           <th>Price</th>
                                       </tr>
                                   </thead>
                                   <tbody>
                                       %{orderSummary}
                                       <tr>
                                           <td colspan="3" class="total-label" style="text-align: left;">Fee shipping</td>
                                           <td class="total-amount" style="color: #c91f28;">%{feeShipping}<sup>đ</sup></td>
                                       </tr>
                                       <tr>
                                           <td colspan="3" class="total-label" style="text-align: left;">Total</td>
                                           <td class="total-amount" style="color: #c91f28;">%{totalAmount}<sup>đ</sup></td>
                                       </tr>
                                       <tr>
                                           <td colspan="3" class="total-label" style="text-align: left; ">Pay</td>
                                           <td class="total-amount" style="color: #c91f28;">%{pay}</td>
                                       </tr>
                                   </tbody>
                               </table>
                               <div style="display: flex; margin: 10px 0px;">
                                   <p style="min-width: 150px; margin: 0px;">Phone number: </p>
                                   <span style="font-weight: bold;">%{phoneNumber}</span>
                               </div>
                               <div style="display: flex; margin: 10px 0px;">
                                   <p style="min-width: 150px; margin: 0px;">Payment: </p>
                                   <span style="font-weight: bold;">%{paymentMethod}</span>
                               </div>
                               <div style="display: flex; margin: 10px 0px;">
                                   <p style="min-width: 150px; margin: 0px;">Order Status: </p>
                                   <span style="font-weight: bold;">%{orderStatus}</span>
                               </div>
                               <div style="display: flex; margin: 10px 0px;">
                                   <p style="min-width: 150px; margin: 0px;">Order placed on: </p>
                                   <span style="font-weight: bold;">%{orderDate}</span>
                               </div>
                               <div style="display: flex; margin: 10px 0px;">
                                   <p style="min-width: 150px; margin: 0px;">Order code: </p>
                                   <span style="font-weight: bold; color: #c91f28;">%{orderCode}</span>
                               </div>
                           </div>
                       </body>
                       </html>
                """;
        // Replace placeholders with actual values
        message = message.replace("%{fullName}", orderResponse.getFullName())
                .replace("%{orderSummary}", orderSummary.toString())
                .replace("%{pay}", orderResponse.getPay())
                .replace("%{phoneNumber}", orderResponse.getPhoneNumber())
                .replace("%{paymentMethod}", orderResponse.getPaymentMethod())
                .replace("%{orderStatus}", orderResponse.getStatusOrder())
                .replace("%{feeShipping}", decimalFormat.format(Math.round(orderResponse.getTransportFee())))
                .replace("%{orderDate}", orderResponse.getOrderDate().format(formatter))
                .replace("%{orderCode}", orderResponse.getCode())
                .replace("%{totalAmount}", decimalFormat.format(Math.round(orderResponse.getTotalPrice())));

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(orderResponse.getEmail());
        mimeMessageHelper.setSubject("Your Order Confirmation from Fashion Shoes");
        mimeMessageHelper.setText(message, true);

        javaMailSender.send(mimeMessage);
    }
}
