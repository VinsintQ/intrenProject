package com.example.cryptopeak.model.request;

import lombok.Data;

@Data
public class DepositRequest {
    private Double amount;
    private String currency; // USD, EUR, etc.
}
