package com.example.cryptopeak.repository;

import com.example.cryptopeak.model.CryptoTransaction;
import com.example.cryptopeak.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoTransactionRepository extends JpaRepository<CryptoTransaction, Long> {
    List<CryptoTransaction> findByUserOrderByCreatedAtDesc(User user);
}
