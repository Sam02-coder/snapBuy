package com.snapBuy.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.snapBuy.common.enums.OrderStatus;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private static final int OTP_EXPIRY_MINUTES = 5;

    @Async("emailExecutor")
    public void sendOtpEmail(String toEmail, String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("expiryMinutes", OTP_EXPIRY_MINUTES);
        send(toEmail, "Verify your email - OTP inside", "email/otp-verification", context);
    }

    @Async("emailExecutor")
    public void sendForgotPasswordOtpEmail(String toEmail, String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("expiryMinutes", OTP_EXPIRY_MINUTES);
        send(toEmail, "Password reset code", "email/forgot-password", context);
    }

    @Async("emailExecutor")
    public void sendMerchantCredentialsEmail(String toEmail, String businessName, String tempPassword) {
        Context context = new Context();
        context.setVariable("businessName", businessName);
        context.setVariable("email", toEmail);
        context.setVariable("tempPassword", tempPassword);
        send(toEmail, "Your merchant account has been created", "email/merchant-credentials", context);
    }

    @Async("emailExecutor")
    public void sendPasswordChangedEmail(String toEmail) {
        send(toEmail, "Your password was changed", "email/password-changed", new Context());
    }

    @Async("emailExecutor")
    public void sendOrderConfirmationEmail(String toEmail, Long orderId, BigDecimal totalAmount) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("totalAmount", totalAmount);
        send(toEmail, "Order confirmed - #" + orderId, "email/order-confirmation", context);
    }

    @Async("emailExecutor")
    public void sendOrderStatusUpdateEmail(String toEmail, Long orderId, OrderStatus status) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("status", status.name());
        send(toEmail, "Update on your order #" + orderId, "email/order-status-update", context);
    }

    private void send(String toEmail, String subject, String templateName, Context context) {
        try {
            String html = templateEngine.process(templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            // Deliberately not rethrown - email failure shouldn't roll back the
            // triggering transaction (e.g. registration). Logged for ops to catch.
            log.error("Failed to send email to {}: {}", toEmail, ex.getMessage());
        }
    }
}