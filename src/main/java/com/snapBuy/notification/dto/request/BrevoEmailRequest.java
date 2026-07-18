package com.snapBuy.notification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrevoEmailRequest {

    private Sender sender;
    private List<Recipient> to;
    private String subject;
    private String htmlContent;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sender {
        private String name;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recipient {
        private String email;
    }
}