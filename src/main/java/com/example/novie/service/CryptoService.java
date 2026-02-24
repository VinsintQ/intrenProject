package com.example.novie.service;

import com.example.novie.model.CryptoCurrency;
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

    public CryptoHistoricalResponse getHistoricalPrices(String cryptoId, String currency, int days) {
        String url = String.format("https://api.coingecko.com/api/v3/coins/%s/market_chart?vs_currency=%s&days=%d",
                cryptoId, currency.toLowerCase(), days);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map>() {}
        );

        Map<String, Object> data = response.getBody();
        List<CryptoHistoricalPrice> prices = new ArrayList<>();

        if (data != null && data.containsKey("prices")) {
            List<List<Number>> priceData = (List<List<Number>>) data.get("prices");
            for (List<Number> entry : priceData) {
                Long timestamp = entry.get(0).longValue();
                Double price = entry.get(1).doubleValue();
                prices.add(new CryptoHistoricalPrice(timestamp, price));
            }
        }

        String period = days == 1 ? "24 hours" : days + " days";
        return new CryptoHistoricalResponse(cryptoId, cryptoId, currency, period, prices);
    }

    public CryptoHistoricalResponse getHistoricalPricesByDateRange(String cryptoId, String currency, long fromTimestamp, long toTimestamp) {
        String url = String.format("https://api.coingecko.com/api/v3/coins/%s/market_chart/range?vs_currency=%s&from=%d&to=%d",
                cryptoId, currency.toLowerCase(), fromTimestamp, toTimestamp);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map>() {}
        );

        Map<String, Object> data = response.getBody();
        List<CryptoHistoricalPrice> prices = new ArrayList<>();

        if (data != null && data.containsKey("prices")) {
            List<List<Number>> priceData = (List<List<Number>>) data.get("prices");
            for (List<Number> entry : priceData) {
                Long timestamp = entry.get(0).longValue();
                Double price = entry.get(1).doubleValue();
                prices.add(new CryptoHistoricalPrice(timestamp, price));
            }
        }

        return new CryptoHistoricalResponse(cryptoId, cryptoId, currency, "custom range", prices);
    }
}
