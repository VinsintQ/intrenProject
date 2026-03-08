package com.example.cryptopeak.model.response;

import com.example.cryptopeak.model.CryptoHistoricalPrice;
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
