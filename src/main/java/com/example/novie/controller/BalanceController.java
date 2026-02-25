package com.example.novie.controller;

import com.example.novie.model.User;
import com.example.novie.repository.UserRepository;
import com.example.novie.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/balance")
public class BalanceController {

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

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserBalance() {
        User user = getCurrentUser();

        Map<String, Object> response = new HashMap<>();
        response.put("balance", user.getBalance());
        response.put("userId", user.getId());
        response.put("userName", user.getUserName());
        response.put("email", user.getEmailAddress());

        return ResponseEntity.ok(response);
    }
}
