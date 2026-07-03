package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.AuthRequest;
import com.gabr.ecommerce.dto.AuthResponse;
import com.gabr.ecommerce.entity.RefreshToken;
import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Role;
import com.gabr.ecommerce.repository.RoleRepository;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.security.JwtService;
import com.gabr.ecommerce.service.RefreshTokenService;
import com.gabr.ecommerce.service.impl.EmailServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailServiceImpl emailService;
    private final RoleRepository roleRepository;

    @Operation(summary = "Login user", security = @SecurityRequirement(name = "none"))
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(@Valid @RequestBody AuthRequest request) {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        AppUser user = AppUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();
        userRepository.save(user);
        emailService.sendWelcomeEmail(request.getEmail(),request.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
    //    String refreshToken = jwtService.generateRefreshToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(
                ApiResponse.success("User registered successfully",
                        AuthResponse.builder()
                                .id(user.getId())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getToken())
                                .email(user.getEmail())
                                .roles(
                                        user.getRoles()
                                                .stream()
                                                .map(Role::getName)
                                                .toList()
                                )
                                .build())
        );
    }
    @Operation(summary = "Login user", security = @SecurityRequirement(name = "none"))
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful",
                        AuthResponse.builder()
                                .id(user.getId())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getToken())
                                .email(user.getEmail())
                                .roles(
                                        user.getRoles()
                                                .stream()
                                                .map(Role::getName)
                                                .toList()
                                )
                                .build())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestParam(required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("refreshToken is required"));
        }

        // 1) validate against DB expiry
        if (!refreshTokenService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid refresh token"));
        }

        // 2) parse jwt
        String email = jwtService.extractEmail(refreshToken);

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 3) validate jwt matches username + not expired
        if (!jwtService.isTokenValid(refreshToken, new User(user.getEmail(), user.getPassword(), List.of()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid refresh token"));
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(
                ApiResponse.success("Access token refreshed",
                        AuthResponse.builder()
                                .id(user.getId())
                                .accessToken(newAccessToken)
                                .refreshToken(refreshToken)
                                .email(user.getEmail())
                                .roles(
                                        user.getRoles()
                                                .stream()
                                                .map(Role::getName)
                                                .toList()
                                )
                                .build())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestParam String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        refreshTokenService.deleteByUser(user);
        return ResponseEntity.ok(ApiResponse.success("User logged out successfully", null));
    }

}
