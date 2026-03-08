package com.example.cryptopeak.model.request;

import lombok.Data;

@Data
public class CryptoTradeRequest {
    private String cryptoId; // bitcoin, ethereum, etc.
    private Double quantity; // amount of crypto to buy/sell
}
