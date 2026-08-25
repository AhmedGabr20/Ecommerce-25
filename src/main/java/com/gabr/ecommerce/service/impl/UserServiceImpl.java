package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.UserListResponse;
import com.gabr.ecommerce.entity.Role;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public void updateUser(Long id, String username, String email) {

    }

    @Override
    public void updatePassword(Long id, String newPassword) {

    }

    @Override
    public void updateRole(Long id, String role) {

    }

    @Override
    public void updateEnabled(Long id, boolean enabled) {

    }

    @Override
    public void updateBlocked(Long id, boolean blocked) {

    }

    @Override
    public Page<UserListResponse> getUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(user -> UserListResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .enabled(user.getEnabled())
                        .createdAt(user.getCreatedAt())
                        .roles(
                                user.getRoles()
                                        .stream()
                                        .map(Role::getName)
                                        .collect(Collectors.toSet())
                        )
                        .build());
    }

}
