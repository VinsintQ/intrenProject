package com.example.novie.controller;

import com.example.novie.model.CryptoHolding;
import com.example.novie.model.CryptoTransaction;
import com.example.novie.model.TradingAlert;
import com.example.novie.model.User;
import com.example.novie.model.request.CryptoTradeRequest;
import com.example.novie.model.response.TradingAnalysisReport;
import com.example.novie.repository.UserRepository;
import com.example.novie.security.MyUserDetails;
import com.example.novie.service.CryptoTradingService;
import com.example.novie.service.TradingSenseiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/trading")
public class CryptoTradingController {

    @Autowired
    private CryptoTradingService tradingService;

    @Autowired
    private TradingSenseiService senseiService;

    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<List<CryptoHolding>> getUserHoldings() {
        User user = getCurrentUser();
        List<CryptoHolding> holdings = tradingService.getUserHoldings(user);
        return ResponseEntity.ok(holdings);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<CryptoTransaction>> getUserTransactions() {
        User user = getCurrentUser();
        List<CryptoTransaction> transactions = tradingService.getUserTransactions(user);
        return ResponseEntity.ok(transactions);
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
