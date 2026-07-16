package com.snapBuy.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RazorpayConfig {

    @Value("${app.razorpay.key-id}")
    private String keyId;

    @Value("${app.razorpay.key-secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        if (keyId == null || keyId.isBlank()) {
            log.warn("Razorpay key-id is not configured - payment endpoints will fail until "
                    + "RAZORPAY_KEY_ID/RAZORPAY_KEY_SECRET are set.");
        }
        return new RazorpayClient(keyId, keySecret);
    }
}