package com.example.Entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String refreshTokenCode;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public RefreshToken() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRefreshTokenCode() {
        return refreshTokenCode;
    }

    public void setRefreshTokenCode(String refreshTokenCode) {
        this.refreshTokenCode = refreshTokenCode;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public RefreshToken(Long id, User user, String refreshTokenCode, LocalDateTime expiryDate) {
        this.id = id;
        this.user = user;
        this.refreshTokenCode = refreshTokenCode;
        this.expiryDate = expiryDate;
    }
}
