package com.example.novie.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimitOrderResponse {
    private Long id;
    private String cryptoId;
    private String cryptoSymbol;
    private String cryptoName;
    private String orderType;
    private Double limitPrice;
    private Double quantity;
    private Double totalAmount;
    private String status;
    private LocalDateTime expiryDate;
    private LocalDateTime filledDate;
    private LocalDateTime createdAt;
    private Double currentPrice; // Current market price for comparison
}
