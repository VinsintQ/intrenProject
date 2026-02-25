package com.example.novie.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingAnalysisReport {
    private String period;
    private Integer totalTrades;
    private Integer profitableTrades;
    private Integer losingTrades;
    private Double winRate;
    private Double totalProfit;
    private Double totalLoss;
    private Double netProfitLoss;
    
    // Time-based analysis
    private Map<String, Double> profitByTimeOfDay;
    private String bestTradingTime;
    private String worstTradingTime;
    
    // Behavioral insights
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;
    
    // Risk analysis
    private Double averageTradeSize;
    private Double largestWin;
    private Double largestLoss;
    private Integer consecutiveLosses;
    private boolean revengeTradingDetected;
}
