package com.example.cryptopeak.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentIntentResponse {
    private String clientSecret;
    private String paymentIntentId;
    private Long depositId;
    private Double amount;
    private String currency;
}
