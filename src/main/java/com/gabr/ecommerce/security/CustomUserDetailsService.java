package com.gabr.ecommerce.security;

import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Permission;
import com.gabr.ecommerce.entity.Role;
import com.gabr.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username:"+ username));
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(user.getIsActive() != null && !user.getIsActive())
                .authorities(extractAuthorities(user))
                .build();
    }

    private Set<GrantedAuthority> extractAuthorities(AppUser user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                // 1. إضافة الـ Role (تجنب تكرار ROLE_ إذا كانت موجودة في الداتابيز)
                String roleName = role.getName().startsWith("ROLE_")
                        ? role.getName()
                        : "ROLE_" + role.getName();
                authorities.add(new SimpleGrantedAuthority(roleName));

                // 2. إضافة الـ Permissions التابعة للـ Role
                if (role.getPermissions() != null) {
                    for (Permission perm : role.getPermissions()) {
                        authorities.add(new SimpleGrantedAuthority(perm.getName()));
                    }
                }
            }
        }

        return authorities;
    }
}
