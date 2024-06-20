package com.example.repository;

import com.example.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Long> {
    List<Cart> findByUserIdAndProductIdAndSize(Long userId, Long productId, int size);

    List<Cart> findByUserId(Long userId);

    List<Cart> findByUserIdOrderByIdDesc(Long userId);

    int countByUserId(Long id);
}
