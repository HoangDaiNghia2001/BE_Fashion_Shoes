package com.example.repository;

import com.example.Entity.RefreshToken;
import com.example.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional <RefreshToken> findByRefreshTokenCode(String refreshTokenCode);

    Optional<RefreshToken> deleteByRefreshTokenCode(String refreshTokenCode);

    @Modifying
    @Query("delete from RefreshToken r where r.expiryDate <= ?1")
    void deleteAllExpiredSince(LocalDateTime now);
}
