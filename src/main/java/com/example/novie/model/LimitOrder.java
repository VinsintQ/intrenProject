package com.example.novie.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "limit_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimitOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @Column(nullable = false)
    private String cryptoId;
    
    @Column(nullable = false)
    private String cryptoSymbol;
    
    @Column(nullable = false)
    private String cryptoName;
    
    @Column(nullable = false)
    private String orderType; // BUY or SELL
    
    @Column(nullable = false)
    private Double limitPrice;
    
    @Column(nullable = false)
    private Double quantity;
    
    @Column(nullable = false)
    private Double totalAmount;
    
    @Column(nullable = false)
    private String status; // PENDING, FILLED, CANCELLED, EXPIRED
    
    @Column
    private LocalDateTime expiryDate;
    
    @Column
    private LocalDateTime filledDate;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
