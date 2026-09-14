package com.gabr.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String avatarUrl;
    private Boolean isActive;
    private Set<String> roles;
    private Set<String> permissions;
    private List<UserAddressResponse> addresses;
}
