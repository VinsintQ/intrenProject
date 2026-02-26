package com.example.novie.service;

import com.example.novie.model.CryptoHistoricalPrice;
import com.example.novie.model.response.CryptoHistoricalResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CryptoHistoryService {

    private final RestTemplate restTemplate;
    private static final String BINANCE_KLINES_URL = "https://api.binance.com/api/v3/klines";
    
    // Map crypto IDs to Binance symbols
    private static final Map<String, String> CRYPTO_ID_TO_SYMBOL = new HashMap<>();
    
    static {
        CRYPTO_ID_TO_SYMBOL.put("bitcoin", "BTC");
        CRYPTO_ID_TO_SYMBOL.put("ethereum", "ETH");
        CRYPTO_ID_TO_SYMBOL.put("binancecoin", "BNB");
        CRYPTO_ID_TO_SYMBOL.put("ripple", "XRP");
        CRYPTO_ID_TO_SYMBOL.put("cardano", "ADA");
        CRYPTO_ID_TO_SYMBOL.put("dogecoin", "DOGE");
        CRYPTO_ID_TO_SYMBOL.put("solana", "SOL");
        CRYPTO_ID_TO_SYMBOL.put("tron", "TRX");
        CRYPTO_ID_TO_SYMBOL.put("polkadot", "DOT");
        CRYPTO_ID_TO_SYMBOL.put("polygon", "MATIC");
        CRYPTO_ID_TO_SYMBOL.put("litecoin", "LTC");
        CRYPTO_ID_TO_SYMBOL.put("shiba-inu", "SHIB");
        CRYPTO_ID_TO_SYMBOL.put("avalanche", "AVAX");
        CRYPTO_ID_TO_SYMBOL.put("uniswap", "UNI");
        CRYPTO_ID_TO_SYMBOL.put("chainlink", "LINK");
        CRYPTO_ID_TO_SYMBOL.put("cosmos", "ATOM");
        CRYPTO_ID_TO_SYMBOL.put("stellar", "XLM");
        CRYPTO_ID_TO_SYMBOL.put("tether", "USDT");
        CRYPTO_ID_TO_SYMBOL.put("usd-coin", "USDC");
        CRYPTO_ID_TO_SYMBOL.put("bitcoin-cash", "BCH");
        
        // Also support direct symbol lookup
        CRYPTO_ID_TO_SYMBOL.put("btc", "BTC");
        CRYPTO_ID_TO_SYMBOL.put("eth", "ETH");
        CRYPTO_ID_TO_SYMBOL.put("bnb", "BNB");
        CRYPTO_ID_TO_SYMBOL.put("xrp", "XRP");
        CRYPTO_ID_TO_SYMBOL.put("ada", "ADA");
        CRYPTO_ID_TO_SYMBOL.put("doge", "DOGE");
        CRYPTO_ID_TO_SYMBOL.put("sol", "SOL");
        CRYPTO_ID_TO_SYMBOL.put("trx", "TRX");
        CRYPTO_ID_TO_SYMBOL.put("dot", "DOT");
        CRYPTO_ID_TO_SYMBOL.put("matic", "MATIC");
        CRYPTO_ID_TO_SYMBOL.put("ltc", "LTC");
        CRYPTO_ID_TO_SYMBOL.put("shib", "SHIB");
        CRYPTO_ID_TO_SYMBOL.put("avax", "AVAX");
        CRYPTO_ID_TO_SYMBOL.put("uni", "UNI");
        CRYPTO_ID_TO_SYMBOL.put("link", "LINK");
        CRYPTO_ID_TO_SYMBOL.put("atom", "ATOM");
        CRYPTO_ID_TO_SYMBOL.put("xlm", "XLM");
        CRYPTO_ID_TO_SYMBOL.put("usdt", "USDT");
        CRYPTO_ID_TO_SYMBOL.put("usdc", "USDC");
        CRYPTO_ID_TO_SYMBOL.put("bch", "BCH");
    }

    public CryptoHistoryService() {
        this.restTemplate = new RestTemplate();
    }

    public CryptoHistoricalResponse getHistoricalPrices(String cryptoId, int days) {
        try {
            System.out.println("=== Fetching historical data for: " + cryptoId + " ===");
            
            // Convert to Binance symbol
            String symbol = CRYPTO_ID_TO_SYMBOL.getOrDefault(cryptoId.toLowerCase(), cryptoId.toUpperCase());
            String tradingPair = symbol + "USDT";
            
            System.out.println("Using Binance trading pair: " + tradingPair);
            
            // Determine interval based on days
            String interval;
            int limit;
            if (days <= 1) {
                interval = "15m"; // 15 minute candles
                limit = 96; // 24 hours
            } else if (days <= 7) {
                interval = "1h"; // 1 hour candles
                limit = days * 24;
            } else if (days <= 30) {
                interval = "4h"; // 4 hour candles
                limit = days * 6;
            } else {
                interval = "1d"; // 1 day candles
                limit = days;
            }
            
            // Binance klines API
            String url = String.format("%s?symbol=%s&interval=%s&limit=%d",
                    BINANCE_KLINES_URL, tradingPair, interval, limit);

            System.out.println("Fetching from Binance URL: " + url);

            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List>() {}
            );

            System.out.println("Response status: " + response.getStatusCode());
            
            List<List<Object>> klines = response.getBody();
            List<CryptoHistoricalPrice> prices = new ArrayList<>();

            if (klines != null && !klines.isEmpty()) {
                System.out.println("Processing " + klines.size() + " klines");
                for (List<Object> kline : klines) {
                    try {
                        // Binance kline format: [timestamp, open, high, low, close, volume, ...]
                        Long timestamp = ((Number) kline.get(0)).longValue();
                        Double closePrice = Double.parseDouble((String) kline.get(4));
                        prices.add(new CryptoHistoricalPrice(timestamp, closePrice));
                    } catch (Exception e) {
                        System.err.println("Error parsing kline: " + e.getMessage());
                    }
                }
            } else {
                System.err.println("No klines data returned from Binance");
            }

            System.out.println("Successfully fetched " + prices.size() + " price points from Binance");

            String period = days + " days";
            return new CryptoHistoricalResponse(cryptoId, symbol, "usd", period, prices);
        } catch (Exception e) {
            System.err.println("=== ERROR fetching historical data from Binance for " + cryptoId + " ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            
            // Return empty response instead of throwing
            return new CryptoHistoricalResponse(cryptoId, cryptoId, "usd", days + " days", new ArrayList<>());
        }
    }
}
