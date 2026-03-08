package com.example.cryptopeak.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoHistoricalPrice {
    private Long timestamp;
    private Double price;
}
