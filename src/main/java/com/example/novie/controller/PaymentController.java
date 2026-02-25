package com.example.novie.controller;

import com.example.novie.model.Deposit;
import com.example.novie.model.User;
import com.example.novie.model.request.DepositRequest;
import com.example.novie.repository.UserRepository;
import com.example.novie.security.MyUserDetails;
import com.example.novie.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof MyUserDetails) {
            return ((MyUserDetails) principal).getUser();
        } else if (principal instanceof String) {
            return userRepository.findUserByEmailAddress((String) principal);
        }
        throw new RuntimeException("Unable to get current user");
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> createDeposit(@RequestBody DepositRequest request) {
        try {
            User user = getCurrentUser();
            Deposit deposit = paymentService.createVirtualDeposit(user, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deposit", deposit);
            response.put("newBalance", user.getBalance());
            response.put("message", "Deposit successful!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Deposit failed: " + e.getMessage());
        }
    }

    @GetMapping("/deposits")
    public ResponseEntity<List<Deposit>> getUserDeposits() {
        User user = getCurrentUser();
        List<Deposit> deposits = paymentService.getUserDeposits(user);
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

    @PostMapping("/reset-balance")
    public ResponseEntity<?> resetBalance() {
        try {
            User user = getCurrentUser();
            User updatedUser = paymentService.resetBalance(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("balance", updatedUser.getBalance());
            response.put("message", "Balance reset to 0");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Reset failed: " + e.getMessage());
        }
    }
}
