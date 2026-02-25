package com.example.novie.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoTransactionResponse {
    private Long id;
    private String transactionType;
    private String cryptoId;
    private String cryptoSymbol;
    private Double quantity;
    private Double pricePerUnit;
    private Double totalAmount;
    private String transactionDate;
}
