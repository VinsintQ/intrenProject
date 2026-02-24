package com.example.novie.controller;

import com.example.novie.model.CryptoCurrency;
import com.example.novie.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/crypto")
public class CryptoController {

    private CryptoService cryptoService;

    @Autowired
    public void setCryptoService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/top")
    public ResponseEntity<List<CryptoCurrency>> getTopCryptocurrencies(
            @RequestParam(defaultValue = "10") int limit) {
        
        System.out.println("Fetching top " + limit + " cryptocurrencies");
        List<CryptoCurrency> cryptos = cryptoService.getTopCryptocurrencies(limit);
        return ResponseEntity.ok(cryptos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CryptoCurrency> getCryptocurrencyById(@PathVariable String id) {
        System.out.println("Fetching cryptocurrency: " + id);
        CryptoCurrency crypto = cryptoService.getCryptocurrencyById(id);
        
        if (crypto != null) {
            return ResponseEntity.ok(crypto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
