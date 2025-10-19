package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.RefreshToken;
import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.repository.RefreshTokenRepository;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.security.JwtService;
import com.gabr.ecommerce.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService ;

    @Override
    public RefreshToken createRefreshToken(AppUser user) {
        String refreshJwt = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshJwt)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }


    @Override
    public boolean validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }
        return true;
    }

    @Override
    @Transactional
    public void deleteByUser(AppUser user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
