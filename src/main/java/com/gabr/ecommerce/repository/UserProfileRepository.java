package com.gabr.ecommerce.repository;

import com.gabr.ecommerce.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository
        extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findUserProfileByUser_Id(Long userId);
}
