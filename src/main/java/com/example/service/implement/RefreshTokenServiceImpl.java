package com.example.service.implement;

import com.example.Entity.RefreshToken;
import com.example.Entity.User;
import com.example.exception.CustomException;
import com.example.repository.RefreshTokenRepository;
import com.example.repository.UserRepository;
import com.example.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<RefreshToken> findByRefreshTokenCode(String refreshTokenCode) {
        return refreshTokenRepository.findByRefreshTokenCode(refreshTokenCode);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) throws CustomException {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user.get());
            refreshToken.setExpiryDate(LocalDateTime.now().plusDays(10));
            refreshToken.setRefreshTokenCode(UUID.randomUUID().toString() + "-" + user.get().getId());

            return refreshTokenRepository.save(refreshToken);
        }else {
            throw new CustomException("User not found with id: " + userId);
        }
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) throws CustomException {
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new CustomException("Refresh token was expired. Please make a new sign in request");
        }
        return refreshToken;
    }

    @Override
    @Transactional
    public String deleteRefreshTokenByRefreshTokenCode(String refreshTokenCode) throws CustomException {
        Optional<RefreshToken> delete = refreshTokenRepository.deleteByRefreshTokenCode(refreshTokenCode);
        if(delete.isPresent()){
            return "Delete refresh token success !!!!";
        }else{
            throw new CustomException("System error !!!");
        }
    }

    @Override
    @Transactional
    public void deleteAllExpiredSince(LocalDateTime now) {
        refreshTokenRepository.deleteAllExpiredSince(now);
    }
}
