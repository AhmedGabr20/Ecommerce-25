package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCartUuidAndUser(UUID cartUuid, AppUser user);
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user = :user")
    Optional<Cart> findByUser(AppUser user);
    Optional<Cart> findByCartUuid(UUID cartUuid);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user IS NULL AND c.lastModifiedAt < :threshold")
    void deleteOldGuestCarts(@Param("threshold") LocalDateTime threshold);
}
