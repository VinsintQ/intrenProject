package com.example.novie.service;

import com.example.novie.model.CryptoTransaction;
import com.example.novie.model.TradingAlert;
import com.example.novie.model.User;
import com.example.novie.model.response.TradingAnalysisReport;
import com.example.novie.repository.CryptoTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradingSenseiService {

    @Autowired
    private CryptoTransactionRepository transactionRepository;

    public TradingAlert checkEmotionalState(User user) {
        List<CryptoTransaction> recentTransactions = getRecentTransactions(user, 10);
        
        if (recentTransactions.size() < 3) {
            return null;
        }

        // Check for revenge trading pattern
        int consecutiveLosses = countConsecutiveLosses(recentTransactions);
        
        if (consecutiveLosses >= 3) {
            CryptoTransaction lastTrade = recentTransactions.get(0);
            double avgTradeSize = calculateAverageTradeSize(recentTransactions.subList(1, 4));
            
            // Check if next trade is significantly larger (potential revenge trading)
            if (lastTrade.getTotalAmount() > avgTradeSize * 2) {
                return new TradingAlert(
                    "DANGER",
                    "⚠️ Revenge Trading Detected!",
                    "You've lost 3 trades in a row and your last trade was " + 
                    String.format("%.0f%%", ((lastTrade.getTotalAmount() / avgTradeSize - 1) * 100)) + 
                    " larger than your average.",
                    "Take a break. Review your last 3 trades. Consider reducing position size.",
                    true
                );
            }
            
            return new TradingAlert(
                "WARNING",
                "🤔 Losing Streak Alert",
                "You've had " + consecutiveLosses + " consecutive losses. This might affect your judgment.",
                "Review your strategy before the next trade. Consider taking a 15-minute break.",
                false
            );
        }

        // Check for overtrading
        List<CryptoTransaction> todayTrades = getTodayTransactions(user);
        if (todayTrades.size() >= 10) {
            return new TradingAlert(
                "WARNING",
                "📊 Overtrading Alert",
                "You've made " + todayTrades.size() + " trades today. Quality over quantity!",
                "Consider focusing on fewer, higher-quality setups.",
                false
            );
        }

        return null;
    }

    public TradingAnalysisReport generateWeeklyReport(User user) {
        LocalDateTime weekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        List<CryptoTransaction> weekTransactions = transactionRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(t -> t.getCreatedAt().isAfter(weekAgo))
                .collect(Collectors.toList());

        if (weekTransactions.isEmpty()) {
            return createEmptyReport();
        }

        TradingAnalysisReport report = new TradingAnalysisReport();
        report.setPeriod("Last 7 Days");
        report.setTotalTrades(weekTransactions.size());

        // Calculate profit/loss for each trade
        Map<Long, Double> tradeProfitLoss = calculateTradeProfitLoss(weekTransactions);
        
        int profitable = (int) tradeProfitLoss.values().stream().filter(pl -> pl > 0).count();
        int losing = (int) tradeProfitLoss.values().stream().filter(pl -> pl < 0).count();
        
        report.setProfitableTrades(profitable);
        report.setLosingTrades(losing);
        report.setWinRate(weekTransactions.size() > 0 ? (profitable * 100.0 / weekTransactions.size()) : 0);

        double totalProfit = tradeProfitLoss.values().stream().filter(pl -> pl > 0).mapToDouble(Double::doubleValue).sum();
        double totalLoss = Math.abs(tradeProfitLoss.values().stream().filter(pl -> pl < 0).mapToDouble(Double::doubleValue).sum());
        
        report.setTotalProfit(totalProfit);
        report.setTotalLoss(totalLoss);
        report.setNetProfitLoss(totalProfit - totalLoss);

        // Time-based analysis
        Map<String, Double> profitByTime = analyzeProfitByTimeOfDay(weekTransactions, tradeProfitLoss);
        report.setProfitByTimeOfDay(profitByTime);
        
        String bestTime = findBestTradingTime(profitByTime);
        String worstTime = findWorstTradingTime(profitByTime);
        report.setBestTradingTime(bestTime);
        report.setWorstTradingTime(worstTime);

        // Risk analysis
        report.setAverageTradeSize(weekTransactions.stream()
                .mapToDouble(CryptoTransaction::getTotalAmount).average().orElse(0));
        report.setLargestWin(tradeProfitLoss.values().stream().max(Double::compare).orElse(0.0));
        report.setLargestLoss(tradeProfitLoss.values().stream().min(Double::compare).orElse(0.0));
        report.setConsecutiveLosses(countConsecutiveLosses(weekTransactions));
        report.setRevengeTradingDetected(detectRevengeTradingPattern(weekTransactions));

        // Generate insights
        report.setStrengths(generateStrengths(report));
        report.setWeaknesses(generateWeaknesses(report));
        report.setRecommendations(generateRecommendations(report));

        return report;
    }

    private List<CryptoTransaction> getRecentTransactions(User user, int limit) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<CryptoTransaction> getTodayTransactions(User user) {
        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        return transactionRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(t -> t.getCreatedAt().isAfter(startOfDay))
                .collect(Collectors.toList());
    }

    private int countConsecutiveLosses(List<CryptoTransaction> transactions) {
        if (transactions.size() < 2) return 0;
        
        int losses = 0;
        Map<String, List<CryptoTransaction>> groupedByCrypto = transactions.stream()
                .collect(Collectors.groupingBy(CryptoTransaction::getCryptoId));
        
        for (List<CryptoTransaction> cryptoTrades : groupedByCrypto.values()) {
            if (cryptoTrades.size() < 2) continue;
            
            cryptoTrades.sort(Comparator.comparing(CryptoTransaction::getCreatedAt).reversed());
            
            for (int i = 0; i < cryptoTrades.size() - 1; i++) {
                CryptoTransaction sell = cryptoTrades.get(i);
                CryptoTransaction buy = cryptoTrades.get(i + 1);
                
                if (sell.getType().equals("SELL") && buy.getType().equals("BUY")) {
                    if (sell.getPricePerUnit() < buy.getPricePerUnit()) {
                        losses++;
                    } else {
                        break;
                    }
                }
            }
        }
        
        return losses;
    }

    private double calculateAverageTradeSize(List<CryptoTransaction> transactions) {
        return transactions.stream()
                .mapToDouble(CryptoTransaction::getTotalAmount)
                .average()
                .orElse(0);
    }

    private Map<Long, Double> calculateTradeProfitLoss(List<CryptoTransaction> transactions) {
        Map<Long, Double> profitLoss = new HashMap<>();
        Map<String, List<CryptoTransaction>> groupedByCrypto = transactions.stream()
                .collect(Collectors.groupingBy(CryptoTransaction::getCryptoId));
        
        for (List<CryptoTransaction> cryptoTrades : groupedByCrypto.values()) {
            cryptoTrades.sort(Comparator.comparing(CryptoTransaction::getCreatedAt));
            
            for (int i = 0; i < cryptoTrades.size() - 1; i++) {
                CryptoTransaction buy = cryptoTrades.get(i);
                CryptoTransaction sell = cryptoTrades.get(i + 1);
                
                if (buy.getType().equals("BUY") && sell.getType().equals("SELL")) {
                    double pl = (sell.getPricePerUnit() - buy.getPricePerUnit()) * sell.getQuantity();
                    profitLoss.put(sell.getId(), pl);
                }
            }
        }
        
        return profitLoss;
    }

    private Map<String, Double> analyzeProfitByTimeOfDay(List<CryptoTransaction> transactions, Map<Long, Double> profitLoss) {
        Map<String, List<Double>> profitByHour = new HashMap<>();
        
        for (CryptoTransaction tx : transactions) {
            if (!profitLoss.containsKey(tx.getId())) continue;
            
            int hour = tx.getCreatedAt().getHour();
            String timeSlot;
            
            if (hour >= 6 && hour < 12) timeSlot = "Morning (6AM-12PM)";
            else if (hour >= 12 && hour < 18) timeSlot = "Afternoon (12PM-6PM)";
            else if (hour >= 18 && hour < 24) timeSlot = "Evening (6PM-12AM)";
            else timeSlot = "Night (12AM-6AM)";
            
            profitByHour.computeIfAbsent(timeSlot, k -> new ArrayList<>()).add(profitLoss.get(tx.getId()));
        }
        
        Map<String, Double> avgProfitByTime = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : profitByHour.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            avgProfitByTime.put(entry.getKey(), avg);
        }
        
        return avgProfitByTime;
    }

    private String findBestTradingTime(Map<String, Double> profitByTime) {
        return profitByTime.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private String findWorstTradingTime(Map<String, Double> profitByTime) {
        return profitByTime.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private boolean detectRevengeTradingPattern(List<CryptoTransaction> transactions) {
        return countConsecutiveLosses(transactions) >= 3;
    }

    private List<String> generateStrengths(TradingAnalysisReport report) {
        List<String> strengths = new ArrayList<>();
        
        if (report.getWinRate() >= 60) {
            strengths.add("🎯 Strong win rate of " + String.format("%.1f%%", report.getWinRate()));
        }
        
        if (report.getNetProfitLoss() > 0) {
            strengths.add("💰 Net profitable: $" + String.format("%.2f", report.getNetProfitLoss()));
        }
        
        if (report.getBestTradingTime() != null && !report.getBestTradingTime().equals("N/A")) {
            strengths.add("⏰ Most profitable during " + report.getBestTradingTime());
        }
        
        if (strengths.isEmpty()) {
            strengths.add("🌱 You're learning! Every trade is experience.");
        }
        
        return strengths;
    }

    private List<String> generateWeaknesses(TradingAnalysisReport report) {
        List<String> weaknesses = new ArrayList<>();
        
        if (report.getWinRate() < 40) {
            weaknesses.add("📉 Win rate below 40% - review your entry strategy");
        }
        
        if (report.isRevengeTradingDetected()) {
            weaknesses.add("⚠️ Revenge trading pattern detected - emotional decisions");
        }
        
        if (report.getWorstTradingTime() != null && !report.getWorstTradingTime().equals("N/A")) {
            Double worstProfit = report.getProfitByTimeOfDay().get(report.getWorstTradingTime());
            if (worstProfit != null && worstProfit < 0) {
                weaknesses.add("🌙 Avoid trading during " + report.getWorstTradingTime() + " - consistent losses");
            }
        }
        
        if (report.getConsecutiveLosses() >= 3) {
            weaknesses.add("📊 " + report.getConsecutiveLosses() + " consecutive losses - take a break");
        }
        
        return weaknesses;
    }

    private List<String> generateRecommendations(TradingAnalysisReport report) {
        List<String> recommendations = new ArrayList<>();
        
        if (report.getWinRate() < 50) {
            recommendations.add("Focus on quality over quantity - wait for better setups");
            recommendations.add("Review your losing trades to identify patterns");
        }
        
        if (report.getBestTradingTime() != null && !report.getBestTradingTime().equals("N/A")) {
            recommendations.add("Concentrate your trading during " + report.getBestTradingTime());
        }
        
        if (report.isRevengeTradingDetected()) {
            recommendations.add("Implement a 'cool-down' period after 2 consecutive losses");
            recommendations.add("Set a maximum daily loss limit");
        }
        
        if (report.getTotalTrades() > 50) {
            recommendations.add("You're overtrading - reduce frequency, increase analysis time");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Keep a trading journal to track your emotional state");
            recommendations.add("Set clear profit targets and stop losses before each trade");
        }
        
        return recommendations;
    }

    private TradingAnalysisReport createEmptyReport() {
        TradingAnalysisReport report = new TradingAnalysisReport();
        report.setPeriod("Last 7 Days");
        report.setTotalTrades(0);
        report.setRecommendations(Arrays.asList(
            "Start trading to build your track record",
            "Begin with small positions to learn the market"
        ));
        return report;
    }
}
