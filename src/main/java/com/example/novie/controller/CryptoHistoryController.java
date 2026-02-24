package com.example.novie.controller;

import com.example.novie.model.response.CryptoHistoricalResponse;
import com.example.novie.service.CryptoHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/history")
public class CryptoHistoryController {

    @Autowired
    private CryptoHistoryService cryptoHistoryService;

    @GetMapping("/{cryptoId}")
    public ResponseEntity<?> getHistory(
            @PathVariable String cryptoId,
            @RequestParam(defaultValue = "7") int days) {
        
        System.out.println("Fetching " + days + " days history for " + cryptoId);
        
        try {
            CryptoHistoricalResponse history = cryptoHistoryService.getHistoricalPrices(cryptoId, days);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }
}
