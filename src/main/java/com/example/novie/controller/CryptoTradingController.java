package com.example.novie.controller;

import com.example.novie.model.CryptoCurrency;
import com.example.novie.model.CryptoHolding;
import com.example.novie.model.CryptoTransaction;
import com.example.novie.model.TradingAlert;
import com.example.novie.model.User;
import com.example.novie.model.request.CryptoTradeRequest;
import com.example.novie.model.response.CryptoHoldingResponse;
import com.example.novie.model.response.CryptoTransactionResponse;
import com.example.novie.model.response.TradingAnalysisReport;
import com.example.novie.repository.UserRepository;
import com.example.novie.security.MyUserDetails;
import com.example.novie.service.CryptoService;
import com.example.novie.service.CryptoTradingService;
import com.example.novie.service.TradingSenseiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/trading")
public class CryptoTradingController {

    @Autowired
    private CryptoTradingService tradingService;

    @Autowired
    private TradingSenseiService senseiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CryptoService cryptoService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof MyUserDetails) {
            return ((MyUserDetails) principal).getUser();
        } else if (principal instanceof String) {
            return userRepository.findUserByEmailAddress((String) principal);
        }
        throw new RuntimeException("Unable to get current user");
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyCrypto(@RequestBody CryptoTradeRequest request) {
        try {
            User user = getCurrentUser();
            CryptoTransaction transaction = tradingService.buyCrypto(
                user, 
                request.getCryptoId(), 
                request.getQuantity()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transaction", transaction);
            response.put("message", "Purchase successful!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Purchase failed: " + e.getMessage());
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellCrypto(@RequestBody CryptoTradeRequest request) {
        try {
            User user = getCurrentUser();
            CryptoTransaction transaction = tradingService.sellCrypto(
                user, 
                request.getCryptoId(), 
                request.getQuantity()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transaction", transaction);
            response.put("message", "Sale successful!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Sale failed: " + e.getMessage());
        }
    }

    @GetMapping("/holdings")
    public ResponseEntity<List<CryptoHoldingResponse>> getUserHoldings() {
        User user = getCurrentUser();
        List<CryptoHolding> holdings = tradingService.getUserHoldings(user);
        
        List<CryptoHoldingResponse> response = holdings.stream().map(holding -> {
            CryptoHoldingResponse dto = new CryptoHoldingResponse();
            dto.setId(holding.getId());
            dto.setCryptoId(holding.getCryptoId());
            dto.setCryptoSymbol(holding.getCryptoSymbol());
            dto.setQuantity(holding.getQuantity());
            dto.setAverageBuyPrice(holding.getAverageBuyPrice());
            
            // Fetch current price and details
            try {
                CryptoCurrency crypto = cryptoService.getCryptocurrencyById(holding.getCryptoId());
                if (crypto != null) {
                    dto.setCryptoName(crypto.getName());
                    dto.setCurrentPrice(crypto.getCurrentPrice());
                    dto.setImage(crypto.getImage());
                    
                    Double totalValue = holding.getQuantity() * crypto.getCurrentPrice();
                    dto.setTotalValue(totalValue);
                    
                    Double profitLoss = totalValue - (holding.getAverageBuyPrice() * holding.getQuantity());
                    dto.setProfitLoss(profitLoss);
                    
                    Double profitLossPercentage = ((profitLoss / (holding.getAverageBuyPrice() * holding.getQuantity())) * 100);
                    dto.setProfitLossPercentage(profitLossPercentage);
                } else {
                    dto.setCryptoName("Unknown");
                    dto.setCurrentPrice(0.0);
                    dto.setImage("https://via.placeholder.com/200?text=" + holding.getCryptoSymbol());
                    dto.setTotalValue(0.0);
                    dto.setProfitLoss(0.0);
                    dto.setProfitLossPercentage(0.0);
                }
            } catch (Exception e) {
                System.err.println("Error fetching current price for " + holding.getCryptoId() + ": " + e.getMessage());
                dto.setCryptoName("Unknown");
                dto.setCurrentPrice(0.0);
                dto.setImage("https://via.placeholder.com/200?text=" + holding.getCryptoSymbol());
                dto.setTotalValue(0.0);
                dto.setProfitLoss(0.0);
                dto.setProfitLossPercentage(0.0);
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<CryptoTransactionResponse>> getUserTransactions() {
        User user = getCurrentUser();
        List<CryptoTransaction> transactions = tradingService.getUserTransactions(user);
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        List<CryptoTransactionResponse> response = transactions.stream().map(tx -> {
            CryptoTransactionResponse dto = new CryptoTransactionResponse();
            dto.setId(tx.getId());
            dto.setTransactionType(tx.getType());
            dto.setCryptoId(tx.getCryptoId());
            dto.setCryptoSymbol(tx.getCryptoSymbol());
            dto.setQuantity(tx.getQuantity());
            dto.setPricePerUnit(tx.getPricePerUnit());
            dto.setTotalAmount(tx.getTotalAmount());
            dto.setTransactionDate(tx.getCreatedAt() != null ? tx.getCreatedAt().toString() : null);
            return dto;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sensei/check")
    public ResponseEntity<?> checkEmotionalState() {
        try {
            User user = getCurrentUser();
            TradingAlert alert = senseiService.checkEmotionalState(user);
            
            if (alert == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "OK");
                response.put("message", "You're trading well! Keep it up.");
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/sensei/weekly-report")
    public ResponseEntity<?> getWeeklyReport() {
        try {
            User user = getCurrentUser();
            TradingAnalysisReport report = senseiService.generateWeeklyReport(user);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }
}
