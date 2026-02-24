package com.example.novie.controller;

import com.example.novie.model.User;
import com.example.novie.security.MyUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/balance")
public class BalanceController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Map<String, Object> response = new HashMap<>();
        response.put("balance", user.getBalance());
        response.put("userId", user.getId());
        response.put("userName", user.getUserName());
        response.put("email", user.getEmailAddress());

        return ResponseEntity.ok(response);
    }
}
