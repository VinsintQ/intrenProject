package com.example.novie.repository;

import com.example.novie.model.Deposit;
import com.example.novie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByUser(User user);
    Optional<Deposit> findByPaymentIntentId(String paymentIntentId);
}
