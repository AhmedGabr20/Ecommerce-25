package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.RefreshToken;
import com.gabr.ecommerce.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(AppUser user);
    Optional<RefreshToken> findByUser(AppUser user);
}
