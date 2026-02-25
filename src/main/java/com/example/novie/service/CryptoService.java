package com.example.novie.service;

import com.example.novie.model.CryptoCurrency;
import com.example.novie.model.CryptoHistoricalPrice;
import com.example.novie.model.response.CryptoHistoricalResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CryptoService {

    private final RestTemplate restTemplate;
    private static final String BINANCE_API_URL = "https://api.binance.com/api/v3";
    
    // Map common crypto symbols to their full names and icons
    private static final Map<String, String> CRYPTO_NAMES = new HashMap<>();
    private static final Map<String, String> CRYPTO_IDS = new HashMap<>();
    private static final Map<String, String> CRYPTO_ICONS = new HashMap<>();
    
    static {
        CRYPTO_NAMES.put("BTC", "Bitcoin");
        CRYPTO_NAMES.put("ETH", "Ethereum");
        CRYPTO_NAMES.put("BNB", "Binance Coin");
        CRYPTO_NAMES.put("XRP", "Ripple");
        CRYPTO_NAMES.put("ADA", "Cardano");
        CRYPTO_NAMES.put("DOGE", "Dogecoin");
        CRYPTO_NAMES.put("SOL", "Solana");
        CRYPTO_NAMES.put("TRX", "TRON");
        CRYPTO_NAMES.put("DOT", "Polkadot");
        CRYPTO_NAMES.put("MATIC", "Polygon");
        CRYPTO_NAMES.put("LTC", "Litecoin");
        CRYPTO_NAMES.put("SHIB", "Shiba Inu");
        CRYPTO_NAMES.put("AVAX", "Avalanche");
        CRYPTO_NAMES.put("UNI", "Uniswap");
        CRYPTO_NAMES.put("LINK", "Chainlink");
        CRYPTO_NAMES.put("ATOM", "Cosmos");
        CRYPTO_NAMES.put("XLM", "Stellar");
        CRYPTO_NAMES.put("USDT", "Tether");
        CRYPTO_NAMES.put("USDC", "USD Coin");
        CRYPTO_NAMES.put("BCH", "Bitcoin Cash");
        
        // Reverse mapping for ID lookup
        CRYPTO_IDS.put("bitcoin", "BTC");
        CRYPTO_IDS.put("ethereum", "ETH");
        CRYPTO_IDS.put("binancecoin", "BNB");
        CRYPTO_IDS.put("ripple", "XRP");
        CRYPTO_IDS.put("cardano", "ADA");
        CRYPTO_IDS.put("dogecoin", "DOGE");
        CRYPTO_IDS.put("solana", "SOL");
        CRYPTO_IDS.put("tron", "TRX");
        CRYPTO_IDS.put("polkadot", "DOT");
        CRYPTO_IDS.put("polygon", "MATIC");
        CRYPTO_IDS.put("litecoin", "LTC");
        CRYPTO_IDS.put("shiba-inu", "SHIB");
        CRYPTO_IDS.put("avalanche", "AVAX");
        CRYPTO_IDS.put("uniswap", "UNI");
        CRYPTO_IDS.put("chainlink", "LINK");
        CRYPTO_IDS.put("cosmos", "ATOM");
        CRYPTO_IDS.put("stellar", "XLM");
        CRYPTO_IDS.put("tether", "USDT");
        CRYPTO_IDS.put("usd-coin", "USDC");
        CRYPTO_IDS.put("bitcoin-cash", "BCH");
        
        // Icon URLs from CoinGecko CDN
        CRYPTO_ICONS.put("BTC", "https://coin-images.coingecko.com/coins/images/1/large/bitcoin.png");
        CRYPTO_ICONS.put("ETH", "https://coin-images.coingecko.com/coins/images/279/large/ethereum.png");
        CRYPTO_ICONS.put("BNB", "https://coin-images.coingecko.com/coins/images/825/large/bnb-icon2_2x.png");
        CRYPTO_ICONS.put("XRP", "https://coin-images.coingecko.com/coins/images/44/large/xrp-symbol-white-128.png");
        CRYPTO_ICONS.put("ADA", "https://coin-images.coingecko.com/coins/images/975/large/cardano.png");
        CRYPTO_ICONS.put("DOGE", "https://coin-images.coingecko.com/coins/images/5/large/dogecoin.png");
        CRYPTO_ICONS.put("SOL", "https://coin-images.coingecko.com/coins/images/4128/large/solana.png");
        CRYPTO_ICONS.put("TRX", "https://coin-images.coingecko.com/coins/images/1094/large/tron-logo.png");
        CRYPTO_ICONS.put("DOT", "https://coin-images.coingecko.com/coins/images/12171/large/polkadot.png");
        CRYPTO_ICONS.put("MATIC", "https://coin-images.coingecko.com/coins/images/4713/large/polygon.png");
        CRYPTO_ICONS.put("LTC", "https://coin-images.coingecko.com/coins/images/2/large/litecoin.png");
        CRYPTO_ICONS.put("SHIB", "https://coin-images.coingecko.com/coins/images/11939/large/shiba.png");
        CRYPTO_ICONS.put("AVAX", "https://coin-images.coingecko.com/coins/images/12559/large/Avalanche_Circle_RedWhite_Trans.png");
        CRYPTO_ICONS.put("UNI", "https://coin-images.coingecko.com/coins/images/12504/large/uni.jpg");
        CRYPTO_ICONS.put("LINK", "https://coin-images.coingecko.com/coins/images/877/large/chainlink-new-logo.png");
        CRYPTO_ICONS.put("ATOM", "https://coin-images.coingecko.com/coins/images/1481/large/cosmos_hub.png");
        CRYPTO_ICONS.put("XLM", "https://coin-images.coingecko.com/coins/images/100/large/Stellar_symbol_black_RGB.png");
        CRYPTO_ICONS.put("USDT", "https://coin-images.coingecko.com/coins/images/325/large/Tether.png");
        CRYPTO_ICONS.put("USDC", "https://coin-images.coingecko.com/coins/images/6319/large/usdc.png");
        CRYPTO_ICONS.put("BCH", "https://coin-images.coingecko.com/coins/images/780/large/bitcoin-cash-circle.png");
    }

    public CryptoService() {
        this.restTemplate = new RestTemplate();
    }

    public List<CryptoCurrency> getTopCryptocurrencies(int limit) {
        try {
            String url = BINANCE_API_URL + "/ticker/24hr";
            System.out.println("Fetching crypto data from Binance: " + url);

            ResponseEntity<List<Map>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map>>() {}
            );

            List<Map> tickers = response.getBody();
            if (tickers == null) return new ArrayList<>();

            List<CryptoCurrency> cryptos = tickers.stream()
                    .filter(ticker -> {
                        String symbol = (String) ticker.get("symbol");
                        return symbol != null && symbol.endsWith("USDT");
                    })
                    .map(ticker -> {
                        String symbol = (String) ticker.get("symbol");
                        String baseSymbol = symbol.replace("USDT", "");
                        
                        if (!CRYPTO_NAMES.containsKey(baseSymbol)) {
                            return null;
                        }
                        
                        CryptoCurrency crypto = new CryptoCurrency();
                        crypto.setId(baseSymbol.toLowerCase());
                        crypto.setSymbol(baseSymbol);
                        crypto.setName(CRYPTO_NAMES.get(baseSymbol));
                        crypto.setImage(CRYPTO_ICONS.getOrDefault(baseSymbol, "https://via.placeholder.com/200?text=" + baseSymbol));
                        
                        String lastPrice = (String) ticker.get("lastPrice");
                        crypto.setCurrentPrice(lastPrice != null ? Double.parseDouble(lastPrice) : 0.0);
                        
                        String priceChangePercent = (String) ticker.get("priceChangePercent");
                        crypto.setPriceChangePercentage24h(priceChangePercent != null ? Double.parseDouble(priceChangePercent) : 0.0);
                        
                        String volume = (String) ticker.get("quoteVolume");
                        crypto.setTotalVolume(volume != null ? Long.parseLong(volume.split("\\.")[0]) : 0L);
                        
                        String highPrice = (String) ticker.get("highPrice");
                        crypto.setHigh24h(highPrice != null ? Double.parseDouble(highPrice) : 0.0);
                        
                        String lowPrice = (String) ticker.get("lowPrice");
                        crypto.setLow24h(lowPrice != null ? Double.parseDouble(lowPrice) : 0.0);
                        
                        String priceChange = (String) ticker.get("priceChange");
                        crypto.setPriceChange24h(priceChange != null ? Double.parseDouble(priceChange) : 0.0);
                        
                        return crypto;
                    })
                    .filter(Objects::nonNull)
                    .sorted((a, b) -> Double.compare(b.getTotalVolume(), a.getTotalVolume()))
                    .limit(limit)
                    .collect(Collectors.toList());

            System.out.println("Successfully fetched " + cryptos.size() + " cryptocurrencies from Binance");
            return cryptos;
        } catch (Exception e) {
            System.err.println("Error fetching cryptocurrencies from Binance: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public CryptoCurrency getCryptocurrencyById(String id) {
        try {
            System.out.println("Fetching cryptocurrency by ID: " + id);
            
            // Convert ID to symbol
            String symbol = CRYPTO_IDS.getOrDefault(id.toLowerCase(), id.toUpperCase());
            String tradingPair = symbol + "USDT";
            
            System.out.println("Converted ID '" + id + "' to trading pair: " + tradingPair);
            
            String url = BINANCE_API_URL + "/ticker/24hr?symbol=" + tradingPair;
            System.out.println("Request URL: " + url);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map>() {}
            );

            Map<String, Object> ticker = response.getBody();
            if (ticker == null) {
                System.err.println("No data returned from Binance for: " + tradingPair);
                return null;
            }

            CryptoCurrency crypto = new CryptoCurrency();
            crypto.setId(id.toLowerCase());
            crypto.setSymbol(symbol);
            crypto.setName(CRYPTO_NAMES.getOrDefault(symbol, symbol));
            crypto.setImage(CRYPTO_ICONS.getOrDefault(symbol, "https://via.placeholder.com/200?text=" + symbol));
            
            String lastPrice = (String) ticker.get("lastPrice");
            crypto.setCurrentPrice(lastPrice != null ? Double.parseDouble(lastPrice) : 0.0);
            
            String priceChangePercent = (String) ticker.get("priceChangePercent");
            crypto.setPriceChangePercentage24h(priceChangePercent != null ? Double.parseDouble(priceChangePercent) : 0.0);
            
            String volume = (String) ticker.get("quoteVolume");
            crypto.setTotalVolume(volume != null ? Long.parseLong(volume.split("\\.")[0]) : 0L);
            
            String highPrice = (String) ticker.get("highPrice");
            crypto.setHigh24h(highPrice != null ? Double.parseDouble(highPrice) : 0.0);
            
            String lowPrice = (String) ticker.get("lowPrice");
            crypto.setLow24h(lowPrice != null ? Double.parseDouble(lowPrice) : 0.0);
            
            String priceChange = (String) ticker.get("priceChange");
            crypto.setPriceChange24h(priceChange != null ? Double.parseDouble(priceChange) : 0.0);

            System.out.println("Found crypto: " + crypto.getName() + " - Price: $" + crypto.getCurrentPrice());
            return crypto;
        } catch (Exception e) {
            System.err.println("Error fetching cryptocurrency '" + id + "' from Binance: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public CryptoHistoricalResponse getHistoricalPrices(String cryptoId, String currency, int days) {
        // Binance historical data requires different endpoint - simplified for now
        return new CryptoHistoricalResponse(cryptoId, cryptoId, currency, days + " days", new ArrayList<>());
    }

    public CryptoHistoricalResponse getHistoricalPricesByDateRange(String cryptoId, String currency, long fromTimestamp, long toTimestamp) {
        // Binance historical data requires different endpoint - simplified for now
        return new CryptoHistoricalResponse(cryptoId, cryptoId, currency, "custom range", new ArrayList<>());
    }
}
