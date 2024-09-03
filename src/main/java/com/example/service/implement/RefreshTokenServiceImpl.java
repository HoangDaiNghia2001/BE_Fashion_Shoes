package com.example.service.implement;

import com.example.Entity.RefreshToken;
import com.example.Entity.User;
import com.example.repository.RefreshTokenRepository;
import com.example.exception.CustomException;
import com.example.service.RefreshTokenService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserService userService;

    @Override
    public Optional<RefreshToken> findByRefreshTokenCode(String refreshTokenCode) {
        return refreshTokenRepository.findByRefreshTokenCode(refreshTokenCode);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) throws CustomException {
        User user = userService.getById(userId);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(10));
        refreshToken.setRefreshTokenCode(UUID.randomUUID() + "-" + user.getId());

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken refreshToken) throws CustomException {
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new CustomException("Refresh token was expired. Please make a new sign in request",
                    HttpStatus.UNAUTHORIZED.value());
        }
        return refreshToken;
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String refreshTokenCode) throws CustomException {
        refreshTokenRepository.deleteByRefreshTokenCode(refreshTokenCode);
    }

    @Override
    @Transactional
    public void deleteAllExpiredSince(LocalDateTime now) {
        refreshTokenRepository.deleteAllExpiredSince(now);
    }
}
