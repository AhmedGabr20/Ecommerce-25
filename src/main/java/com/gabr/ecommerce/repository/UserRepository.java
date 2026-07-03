package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser,Long> {

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(" select count(u) from AppUser u")
    long countAll();
}
