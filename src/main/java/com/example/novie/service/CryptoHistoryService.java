package com.example.novie.service;

import com.example.novie.model.CryptoHistoricalPrice;
import com.example.novie.model.response.CryptoHistoricalResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CryptoHistoryService {

    private final RestTemplate restTemplate;

    public CryptoHistoryService() {
        this.restTemplate = new RestTemplate();
    }

    public CryptoHistoricalResponse getHistoricalPrices(String cryptoId, int days) {
        // Using CoinGecko API for historical data
        String url = String.format("https://api.coingecko.com/api/v3/coins/%s/market_chart?vs_currency=usd&days=%d",
                cryptoId, days);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map>() {}
        );

        Map<String, Object> body = response.getBody();
        List<CryptoHistoricalPrice> prices = new ArrayList<>();

        if (body != null && body.containsKey("prices")) {
            List<List<Number>> priceData = (List<List<Number>>) body.get("prices");
            for (List<Number> entry : priceData) {
                Long timestamp = entry.get(0).longValue();
                Double price = entry.get(1).doubleValue();
                prices.add(new CryptoHistoricalPrice(timestamp, price));
            }
        }

        String period = days + " days";
        return new CryptoHistoricalResponse(cryptoId, cryptoId, "usd", period, prices);
    }
}
