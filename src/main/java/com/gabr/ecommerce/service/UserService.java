package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.UserListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    Page<UserListResponse> getUsers(Pageable pageable);

    void deleteUser(Long id);

    void updateUser(Long id, String username, String email);

    void updatePassword(Long id, String newPassword);

    void updateRole(Long id, String role);

    void updateEnabled(Long id, boolean enabled);

    void updateBlocked(Long id, boolean blocked);
}
