package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.AuthRequest;
import com.gabr.ecommerce.dto.AuthResponse;
import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Role;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.security.JwtService;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody AuthRequest request) {
        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole())
                .build());
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        AppUser user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole())
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, new User(user.getUsername(), user.getPassword(), List.of())))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String newAccessToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole())
                .build());
    }

}
