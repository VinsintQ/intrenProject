package com.example.novie.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.novie.model.LimitOrder;
import com.example.novie.model.User;

@Repository
public interface LimitOrderRepository extends JpaRepository<LimitOrder, Long> {
    List<LimitOrder> findByUserOrderByCreatedAtDesc(User user);
    List<LimitOrder> findByUserAndStatusOrderByCreatedAtDesc(User user, String status);
    List<LimitOrder> findByStatusOrderByCreatedAtAsc(String status);
}
