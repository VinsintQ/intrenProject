package com.example.novie.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.novie.exception.InformationNotFoundException;
import com.example.novie.model.LimitOrder;
import com.example.novie.model.User;
import com.example.novie.model.request.LimitOrderRequest;
import com.example.novie.model.response.LimitOrderResponse;
import com.example.novie.repository.LimitOrderRepository;
import com.example.novie.repository.UserRepository;
import com.example.novie.security.MyUserDetails;

@Service
public class LimitOrderService {

    @Autowired
    private LimitOrderRepository limitOrderRepository;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private CryptoTradingService cryptoTradingService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    @Transactional
    public LimitOrderResponse createLimitOrder(LimitOrderRequest request) {
        User user = getCurrentUser();

        // Validate order
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (request.getLimitPrice() <= 0) {
            throw new IllegalArgumentException("Limit price must be greater than 0");
        }

        Double totalAmount = request.getQuantity() * request.getLimitPrice();

        // For BUY orders, check if user has enough balance
        if ("BUY".equalsIgnoreCase(request.getOrderType())) {
            if (user.getBalance() < totalAmount) {
                throw new IllegalArgumentException("Insufficient balance for this order");
            }
            // Reserve the funds
            user.setBalance(user.getBalance() - totalAmount);
            userRepository.save(user);
        }

        // For SELL orders, check if user has enough crypto
        if ("SELL".equalsIgnoreCase(request.getOrderType())) {
            // This would need to check holdings - simplified for now
        }

        LimitOrder order = new LimitOrder();
        order.setUser(user);
        order.setCryptoId(request.getCryptoId());
        order.setCryptoSymbol(request.getCryptoSymbol());
        order.setCryptoName(request.getCryptoName());
        order.setOrderType(request.getOrderType().toUpperCase());
        order.setLimitPrice(request.getLimitPrice());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");

        // Set expiry date if provided
        if (request.getExpiryDays() != null && request.getExpiryDays() > 0) {
            order.setExpiryDate(LocalDateTime.now().plusDays(request.getExpiryDays()));
        } else {
            order.setExpiryDate(LocalDateTime.now().plusDays(30)); // Default 30 days
        }

        LimitOrder savedOrder = limitOrderRepository.save(order);
        return convertToResponse(savedOrder);
    }

    public List<LimitOrderResponse> getUserOrders() {
        User user = getCurrentUser();
        List<LimitOrder> orders = limitOrderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<LimitOrderResponse> getPendingOrders() {
        User user = getCurrentUser();
        List<LimitOrder> orders = limitOrderRepository.findByUserAndStatusOrderByCreatedAtDesc(user, "PENDING");
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LimitOrderResponse cancelOrder(Long orderId) {
        User user = getCurrentUser();
        LimitOrder order = limitOrderRepository.findById(orderId)
                .orElseThrow(() -> new InformationNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only cancel your own orders");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalArgumentException("Only pending orders can be cancelled");
        }

        // Refund the reserved funds for BUY orders
        if ("BUY".equalsIgnoreCase(order.getOrderType())) {
            user.setBalance(user.getBalance() + order.getTotalAmount());
            userRepository.save(user);
        }

        order.setStatus("CANCELLED");
        LimitOrder savedOrder = limitOrderRepository.save(order);
        return convertToResponse(savedOrder);
    }

    // This method is called every 30 seconds to check and execute pending orders
    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    @Transactional
    public void checkAndExecutePendingOrders() {
        List<LimitOrder> pendingOrders = limitOrderRepository.findByStatusOrderByCreatedAtAsc("PENDING");
        
        System.out.println("Checking " + pendingOrders.size() + " pending limit orders...");

        for (LimitOrder order : pendingOrders) {
            try {
                // Check if order has expired
                if (order.getExpiryDate() != null && LocalDateTime.now().isAfter(order.getExpiryDate())) {
                    System.out.println("Order " + order.getId() + " has expired");
                    order.setStatus("EXPIRED");
                    // Refund for BUY orders
                    if ("BUY".equalsIgnoreCase(order.getOrderType())) {
                        User user = order.getUser();
                        user.setBalance(user.getBalance() + order.getTotalAmount());
                        userRepository.save(user);
                    }
                    limitOrderRepository.save(order);
                    continue;
                }

                // Get current price
                Double currentPrice = getCurrentPrice(order.getCryptoId());
                if (currentPrice == null) {
                    System.out.println("Could not get current price for " + order.getCryptoId());
                    continue;
                }

                System.out.println("Order " + order.getId() + ": " + order.getOrderType() + 
                    " " + order.getCryptoSymbol() + 
                    " | Limit: $" + order.getLimitPrice() + 
                    " | Current: $" + currentPrice);

                // Check if order should be executed
                boolean shouldExecute = false;
                if ("BUY".equalsIgnoreCase(order.getOrderType()) && currentPrice <= order.getLimitPrice()) {
                    shouldExecute = true;
                    System.out.println("BUY order ready to execute: current price $" + currentPrice + " <= limit $" + order.getLimitPrice());
                } else if ("SELL".equalsIgnoreCase(order.getOrderType()) && currentPrice >= order.getLimitPrice()) {
                    shouldExecute = true;
                    System.out.println("SELL order ready to execute: current price $" + currentPrice + " >= limit $" + order.getLimitPrice());
                }

                if (shouldExecute) {
                    System.out.println("Executing order " + order.getId());
                    executeOrder(order, currentPrice);
                }
            } catch (Exception e) {
                System.err.println("Error processing order " + order.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void executeOrder(LimitOrder order, Double currentPrice) {
        try {
            User user = order.getUser();
            
            if ("BUY".equalsIgnoreCase(order.getOrderType())) {
                // Funds already reserved, just execute the trade
                cryptoTradingService.executeBuyForLimitOrder(user, order.getCryptoId(), order.getQuantity(), currentPrice);
            } else {
                // Execute sell
                cryptoTradingService.executeSellForLimitOrder(user, order.getCryptoId(), order.getQuantity(), currentPrice);
            }

            order.setStatus("FILLED");
            order.setFilledDate(LocalDateTime.now());
            limitOrderRepository.save(order);
        } catch (Exception e) {
            System.err.println("Failed to execute order " + order.getId() + ": " + e.getMessage());
        }
    }

    private Double getCurrentPrice(String cryptoId) {
        try {
            var crypto = cryptoService.getCryptocurrencyById(cryptoId);
            return crypto != null ? crypto.getCurrentPrice() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private LimitOrderResponse convertToResponse(LimitOrder order) {
        LimitOrderResponse response = new LimitOrderResponse();
        response.setId(order.getId());
        response.setCryptoId(order.getCryptoId());
        response.setCryptoSymbol(order.getCryptoSymbol());
        response.setCryptoName(order.getCryptoName());
        response.setOrderType(order.getOrderType());
        response.setLimitPrice(order.getLimitPrice());
        response.setQuantity(order.getQuantity());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setExpiryDate(order.getExpiryDate());
        response.setFilledDate(order.getFilledDate());
        response.setCreatedAt(order.getCreatedAt());

        // Get current price for comparison
        try {
            response.setCurrentPrice(getCurrentPrice(order.getCryptoId()));
        } catch (Exception e) {
            response.setCurrentPrice(null);
        }

        return response;
    }
}
