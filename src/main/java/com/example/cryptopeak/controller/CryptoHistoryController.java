package com.example.cryptopeak.controller;

import com.example.cryptopeak.model.response.CryptoHistoricalResponse;
import com.example.cryptopeak.service.CryptoHistoryService;
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
            @PathVariable(name = "cryptoId") String cryptoId,
            @RequestParam(name = "days", defaultValue = "7") int days) {
        
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
