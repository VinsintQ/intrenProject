package com.example.cryptopeak.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingAlert {
    private String type; // WARNING, INFO, DANGER
    private String title;
    private String message;
    private String recommendation;
    private boolean shouldBlock;
}
