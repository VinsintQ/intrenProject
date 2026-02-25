package com.example.novie.repository;

import com.example.novie.model.CryptoTransaction;
import com.example.novie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoTransactionRepository extends JpaRepository<CryptoTransaction, Long> {
    List<CryptoTransaction> findByUserOrderByCreatedAtDesc(User user);
}
