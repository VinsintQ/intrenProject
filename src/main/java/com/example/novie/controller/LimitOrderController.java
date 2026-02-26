package com.example.novie.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.novie.model.request.LimitOrderRequest;
import com.example.novie.model.response.LimitOrderResponse;
import com.example.novie.service.LimitOrderService;

@RestController
@RequestMapping("/api/limit-orders")
public class LimitOrderController {

    @Autowired
    private LimitOrderService limitOrderService;

    @PostMapping
    public ResponseEntity<LimitOrderResponse> createLimitOrder(@RequestBody LimitOrderRequest request) {
        return ResponseEntity.ok(limitOrderService.createLimitOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<LimitOrderResponse>> getUserOrders() {
        return ResponseEntity.ok(limitOrderService.getUserOrders());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LimitOrderResponse>> getPendingOrders() {
        return ResponseEntity.ok(limitOrderService.getPendingOrders());
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<LimitOrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(limitOrderService.cancelOrder(orderId));
    }
}
