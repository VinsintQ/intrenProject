package com.example.cryptopeak.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cryptopeak.model.Deposit;
import com.example.cryptopeak.model.User;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByUserOrderByCreatedAtDesc(User user);
    Optional<Deposit> findByPaymentIntentId(String paymentIntentId);
}
