package com.example.novie.model.request;

import lombok.Data;

@Data
public class LimitOrderRequest {
    private String cryptoId;
    private String cryptoSymbol;
    private String cryptoName;
    private String orderType; // BUY or SELL
    private Double limitPrice;
    private Double quantity;
    private Integer expiryDays; // Optional: days until order expires
}
