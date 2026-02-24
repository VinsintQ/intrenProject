package com.example.novie.model.response;

import com.example.novie.model.CryptoHistoricalPrice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoHistoricalResponse {
    private String cryptoId;
    private String cryptoName;
    private String currency;
    private String period;
    private List<CryptoHistoricalPrice> prices;
}
