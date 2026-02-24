package com.example.novie.controller;

import com.example.novie.model.Deposit;
import com.example.novie.model.request.DepositRequest;
import com.example.novie.model.response.PaymentIntentResponse;
import com.example.novie.security.MyUserDetails;
import com.example.novie.service.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody DepositRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

            PaymentIntentResponse response = paymentService.createPaymentIntent(
                    userDetails.getUser(), 
                    request
            );

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest()
                    .body("Payment processing error: " + e.getMessage());
        }
    }

    @PostMapping("/confirm/{paymentIntentId}")
    public ResponseEntity<?> confirmPayment(@PathVariable String paymentIntentId) {
        try {
            Deposit deposit = paymentService.confirmPayment(paymentIntentId);
            return ResponseEntity.ok(deposit);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error confirming payment: " + e.getMessage());
        }
    }

    @GetMapping("/deposits")
    public ResponseEntity<List<Deposit>> getUserDeposits() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        List<Deposit> deposits = paymentService.getUserDeposits(userDetails.getUser());
        return ResponseEntity.ok(deposits);
    }

    @GetMapping("/deposits/{id}")
    public ResponseEntity<?> getDepositById(@PathVariable Long id) {
        try {
            Deposit deposit = paymentService.getDepositById(id);
            return ResponseEntity.ok(deposit);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
