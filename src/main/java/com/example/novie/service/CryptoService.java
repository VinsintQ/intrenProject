package com.example.novie.service;

import com.example.novie.model.CryptoCurrency;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class CryptoService {

    private final RestTemplate restTemplate;
    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/coins/markets";

    public CryptoService() {
        this.restTemplate = new RestTemplate();
    }

    public List<CryptoCurrency> getTopCryptocurrencies(int limit) {
        String url = String.format("%s?vs_currency=usd&order=market_cap_desc&per_page=%d&page=1&sparkline=false",
                COINGECKO_API_URL, limit);

        ResponseEntity<List<CryptoCurrency>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CryptoCurrency>>() {}
        );

        return response.getBody();
    }

    public CryptoCurrency getCryptocurrencyById(String id) {
        String url = String.format("%s?vs_currency=usd&ids=%s&order=market_cap_desc&per_page=1&page=1&sparkline=false",
                COINGECKO_API_URL, id);

        ResponseEntity<List<CryptoCurrency>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CryptoCurrency>>() {}
        );

        List<CryptoCurrency> cryptos = response.getBody();
        return (cryptos != null && !cryptos.isEmpty()) ? cryptos.get(0) : null;
    }
}
