package com.example.novie.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CryptoCurrency {
    private String id;
    private String symbol;
    private String name;
    private String image;
    
    @JsonProperty("current_price")
    private Double currentPrice;
    
    @JsonProperty("market_cap")
    private Long marketCap;
    
    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;
    
    @JsonProperty("total_volume")
    private Long totalVolume;
    
    @JsonProperty("high_24h")
    private Double high24h;
    
    @JsonProperty("low_24h")
    private Double low24h;
    
    @JsonProperty("price_change_24h")
    private Double priceChange24h;
    
    @JsonProperty("price_change_percentage_24h")
    private Double priceChangePercentage24h;
}
