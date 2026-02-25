package com.example.novie.repository;

import com.example.novie.model.CryptoHolding;
import com.example.novie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoHoldingRepository extends JpaRepository<CryptoHolding, Long> {
    List<CryptoHolding> findByUser(User user);
    Optional<CryptoHolding> findByUserAndCryptoId(User user, String cryptoId);
}
