package com.example.novie.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.novie.model.CryptoCurrency;
import com.example.novie.model.CryptoHolding;
import com.example.novie.model.CryptoTransaction;
import com.example.novie.model.User;
import com.example.novie.repository.CryptoHoldingRepository;
import com.example.novie.repository.CryptoTransactionRepository;
import com.example.novie.repository.UserRepository;

@Service
public class CryptoTradingService {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private CryptoHoldingRepository holdingRepository;

    @Autowired
    private CryptoTransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CryptoTransaction buyCrypto(User user, String cryptoId, Double quantity) {
        System.out.println("Attempting to buy crypto: " + cryptoId + ", quantity: " + quantity);
        
        // Get current price
        CryptoCurrency crypto = cryptoService.getCryptocurrencyById(cryptoId);
        if (crypto == null) {
            System.err.println("Cryptocurrency not found: " + cryptoId);
            throw new RuntimeException("Cryptocurrency not found: " + cryptoId);
        }

        System.out.println("Found crypto: " + crypto.getName() + " at price: $" + crypto.getCurrentPrice());

        Double pricePerUnit = crypto.getCurrentPrice();
        if (pricePerUnit == null || pricePerUnit <= 0) {
            throw new RuntimeException("Invalid cryptocurrency price");
        }
        
        Double totalCost = pricePerUnit * quantity;
        System.out.println("Total cost: $" + totalCost + ", User balance: $" + user.getBalance());

        // Check if user has enough balance
        if (user.getBalance() < totalCost) {
            throw new RuntimeException("Insufficient balance. Required: $" + totalCost + ", Available: $" + user.getBalance());
        }

        // Deduct from user balance
        user.setBalance(user.getBalance() - totalCost);
        userRepository.save(user);

        // Update or create holding
        Optional<CryptoHolding> existingHolding = holdingRepository.findByUserAndCryptoId(user, cryptoId);
        CryptoHolding holding;

        if (existingHolding.isPresent()) {
            holding = existingHolding.get();
            Double totalQuantity = holding.getQuantity() + quantity;
            Double totalValue = (holding.getQuantity() * holding.getAverageBuyPrice()) + totalCost;
            holding.setAverageBuyPrice(totalValue / totalQuantity);
            holding.setQuantity(totalQuantity);
        } else {
            holding = new CryptoHolding();
            holding.setUser(user);
            holding.setCryptoId(cryptoId);
            holding.setCryptoSymbol(crypto.getSymbol().toUpperCase());
            holding.setQuantity(quantity);
            holding.setAverageBuyPrice(pricePerUnit);
        }
        holdingRepository.save(holding);

        // Create transaction record
        CryptoTransaction transaction = new CryptoTransaction();
        transaction.setUser(user);
        transaction.setType("BUY");
        transaction.setCryptoId(cryptoId);
        transaction.setCryptoSymbol(crypto.getSymbol().toUpperCase());
        transaction.setQuantity(quantity);
        transaction.setPricePerUnit(pricePerUnit);
        transaction.setTotalAmount(totalCost);

        System.out.println("Purchase successful!");
        return transactionRepository.save(transaction);
    }

    @Transactional
    public CryptoTransaction sellCrypto(User user, String cryptoId, Double quantity) {
        // Check if user has the crypto
        CryptoHolding holding = holdingRepository.findByUserAndCryptoId(user, cryptoId)
                .orElseThrow(() -> new RuntimeException("You don't own this cryptocurrency"));

        if (holding.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient crypto balance");
        }

        // Get current price
        CryptoCurrency crypto = cryptoService.getCryptocurrencyById(cryptoId);
        if (crypto == null) {
            throw new RuntimeException("Cryptocurrency not found");
        }

        Double pricePerUnit = crypto.getCurrentPrice();
        Double totalRevenue = pricePerUnit * quantity;

        // Add to user balance
        user.setBalance(user.getBalance() + totalRevenue);
        userRepository.save(user);

        // Update holding
        holding.setQuantity(holding.getQuantity() - quantity);
        if (holding.getQuantity() == 0) {
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }

        // Create transaction record
        CryptoTransaction transaction = new CryptoTransaction();
        transaction.setUser(user);
        transaction.setType("SELL");
        transaction.setCryptoId(cryptoId);
        transaction.setCryptoSymbol(crypto.getSymbol().toUpperCase());
        transaction.setQuantity(quantity);
        transaction.setPricePerUnit(pricePerUnit);
        transaction.setTotalAmount(totalRevenue);

        return transactionRepository.save(transaction);
    }

    public List<CryptoHolding> getUserHoldings(User user) {
        return holdingRepository.findByUser(user);
    }

    public List<CryptoTransaction> getUserTransactions(User user) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // Methods for limit order execution
    @Transactional
    public void executeBuyForLimitOrder(User user, String cryptoId, Double quantity, Double price) {
        // Funds already reserved, just execute the trade
        CryptoCurrency crypto = cryptoService.getCryptocurrencyById(cryptoId);
        if (crypto == null) {
            throw new RuntimeException("Cryptocurrency not found");
        }

        // Update or create holding
        Optional<CryptoHolding> existingHolding = holdingRepository.findByUserAndCryptoId(user, cryptoId);
        CryptoHolding holding;

        if (existingHolding.isPresent()) {
            holding = existingHolding.get();
            Double totalQuantity = holding.getQuantity() + quantity;
            Double totalValue = (holding.getQuantity() * holding.getAverageBuyPrice()) + (quantity * price);
            holding.setAverageBuyPrice(totalValue / totalQuantity);
            holding.setQuantity(totalQuantity);
        } else {
            holding = new CryptoHolding();
            holding.setUser(user);
            holding.setCryptoId(cryptoId);
            holding.setCryptoSymbol(crypto.getSymbol().toUpperCase());
            holding.setQuantity(quantity);
            holding.setAverageBuyPrice(price);
        }
        holdingRepository.save(holding);

        // Create transaction record
        CryptoTransaction transaction = new CryptoTransaction();
        transaction.setUser(user);
        transaction.setType("BUY");
        transaction.setCryptoId(cryptoId);
        transaction.setCryptoSymbol(crypto.getSymbol().toUpperCase());
        transaction.setQuantity(quantity);
        transaction.setPricePerUnit(price);
        transaction.setTotalAmount(quantity * price);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void executeSellForLimitOrder(User user, String cryptoId, Double quantity, Double price) {
        CryptoHolding holding = holdingRepository.findByUserAndCryptoId(user, cryptoId)
                .orElseThrow(() -> new RuntimeException("You don't own this cryptocurrency"));

        if (holding.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient crypto balance");
        }

        CryptoCurrency crypto = cryptoService.getCryptocurrencyById(cryptoId);
        Double totalRevenue = price * quantity;

        // Add to user balance
        user.setBalance(user.getBalance() + totalRevenue);
        userRepository.save(user);

        // Update holding
        holding.setQuantity(holding.getQuantity() - quantity);
        if (holding.getQuantity() == 0) {
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }

        // Create transaction record
        CryptoTransaction transaction = new CryptoTransaction();
        transaction.setUser(user);
        transaction.setType("SELL");
        transaction.setCryptoId(cryptoId);
        transaction.setCryptoSymbol(crypto.getSymbol().toUpperCase());
        transaction.setQuantity(quantity);
        transaction.setPricePerUnit(price);
        transaction.setTotalAmount(totalRevenue);
        transactionRepository.save(transaction);
    }
}
