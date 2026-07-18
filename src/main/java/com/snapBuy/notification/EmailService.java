package com.snapBuy.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.snapBuy.common.enums.OrderStatus;
import com.snapBuy.notification.dto.request.BrevoEmailRequest;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final RestClient restClient;
	private final TemplateEngine templateEngine;

	@Value("${app.brevo.api-key}")
	private String apiKey;

	@Value("${app.brevo.sender-email}")
	private String senderEmail;

	@Value("${app.brevo.sender-name}")
	private String senderName;
    

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

            BrevoEmailRequest request = new BrevoEmailRequest(
                    new BrevoEmailRequest.Sender(senderName, senderEmail),
                    java.util.List.of(new BrevoEmailRequest.Recipient(toEmail)),
                    subject,
                    html
            );

            restClient.post()
                    .uri("https://api.brevo.com/v3/smtp/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Email sent successfully to {}", toEmail);

        } catch (Exception ex) {

            log.error("Failed to send email to {}", toEmail, ex);

        }
    }
}