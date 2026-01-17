package com.gabr.ecommerce.entity;

import com.gabr.ecommerce.dto.RefreshToken;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@ToString(exclude = "refreshTokens")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role ;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens;
}
