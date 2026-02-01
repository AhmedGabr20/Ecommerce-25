package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser,Integer> {
    Optional<AppUser> findByUsername(String username);

    @Query(" select count(u) from AppUser u")
    long countAll();
}
