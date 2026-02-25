package com.example.novie.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_transactions")
@Data
public class CryptoTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String type; // BUY or SELL
    
    @Column(nullable = false)
    private String cryptoId;
    
    @Column(nullable = false)
    private String cryptoSymbol;
    
    @Column(nullable = false)
    private Double quantity;
    
    @Column(nullable = false)
    private Double pricePerUnit;
    
    @Column(nullable = false)
    private Double totalAmount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
