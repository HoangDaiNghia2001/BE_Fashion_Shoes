package com.example.repository;

import com.example.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUserIdAndProductIdAndSize(Long userId, Long productId, int size);

    List<Cart> findByUserId(Long userId);

    List<Cart> findByUserIdOrderByIdDesc(Long userId);

    int countByUserId(Long id);

    @Query("select c from Cart c where c.user.id = ?1 " +
            "and c.id <> ?2 " +
            "and c.product.id = ?3 " +
            "and c.size = ?4")
    Optional<Cart> checkCartItemDuplicate(long userId, long idCart, long productId, int sizeName);
}
