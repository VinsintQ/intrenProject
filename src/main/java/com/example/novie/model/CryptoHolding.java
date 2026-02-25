package com.example.novie.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_holdings")
@Data
public class CryptoHolding {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String cryptoId; // bitcoin, ethereum, etc.
    
    @Column(nullable = false)
    private String cryptoSymbol; // BTC, ETH, etc.
    
    @Column(nullable = false)
    private Double quantity;
    
    @Column(name = "average_buy_price")
    private Double averageBuyPrice;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
