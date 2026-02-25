package com.example.novie.service;

import com.example.novie.model.Deposit;
import com.example.novie.model.User;
import com.example.novie.model.request.DepositRequest;
import com.example.novie.repository.DepositRepository;
import com.example.novie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private UserRepository userRepository;

    public Deposit createVirtualDeposit(User user, DepositRequest request) {
        // Create virtual deposit (no real payment processing)
        Deposit deposit = new Deposit();
        deposit.setUser(user);
        deposit.setAmount(request.getAmount());
        deposit.setCurrency(request.getCurrency());
        deposit.setStatus("COMPLETED");
        deposit.setPaymentIntentId("VIRTUAL-" + UUID.randomUUID().toString());
        
        // Immediately add to user balance
        user.setBalance(user.getBalance() + request.getAmount());
        userRepository.save(user);
        
        return depositRepository.save(deposit);
    }

    public List<Deposit> getUserDeposits(User user) {
        return depositRepository.findByUser(user);
    }

    public Deposit getDepositById(Long id) {
        return depositRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deposit not found"));
    }

    public User resetBalance(User user) {
        user.setBalance(0.0);
        return userRepository.save(user);
    }
}
