package com.gabr.ecommerce.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {

    private Long id;

    private String email;

    private boolean enabled;

    private LocalDateTime createdAt;

    private Set<String> roles;
}