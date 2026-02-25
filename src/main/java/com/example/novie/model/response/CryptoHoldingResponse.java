package com.example.novie.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoHoldingResponse {
    private Long id;
    private String cryptoId;
    private String cryptoSymbol;
    private String cryptoName;
    private String image;
    private Double quantity;
    private Double averageBuyPrice;
    private Double currentPrice;
    private Double totalValue;
    private Double profitLoss;
    private Double profitLossPercentage;
}
