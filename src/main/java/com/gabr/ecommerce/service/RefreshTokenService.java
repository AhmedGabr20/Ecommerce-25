package com.gabr.ecommerce.service;

import com.gabr.ecommerce.entity.RefreshToken;
import com.gabr.ecommerce.entity.AppUser;

public interface RefreshTokenService {
     RefreshToken createRefreshToken(AppUser user);
    boolean validateRefreshToken(String token);
    void deleteByUser(AppUser user);
}
