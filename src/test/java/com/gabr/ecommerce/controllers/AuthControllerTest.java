package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.AuthRequest;
import com.gabr.ecommerce.dto.AuthResponse;
import com.gabr.ecommerce.dto.RefreshToken;
import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Role;
import com.gabr.ecommerce.repository.UserRepository;
import com.gabr.ecommerce.security.JwtService;
import com.gabr.ecommerce.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController authController;

    private AppUser user;
    RefreshToken refreshToken = new RefreshToken();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refreshToken.setToken("refresh123");
        user = AppUser.builder()
                .id(1L)
                .username("test")
                .password("encodedPass")
                .role(Role.USER)
                .build();
    }

   @Test
    void testRegisterUser() {


       when(passwordEncoder.encode(any())).thenReturn("encodedPass");
       when(userRepository.save(any())).thenReturn(user);
       when(jwtService.generateAccessToken(any())).thenReturn("access123");
       when(refreshTokenService.createRefreshToken(any())).thenReturn(refreshToken);

       AuthRequest request = new AuthRequest("test", "1234");
       ResponseEntity<ApiResponse<AuthResponse>> response = authController.registerUser(request);

       assertEquals(200, response.getStatusCodeValue());
       assertEquals("SUCCESS", response.getBody().getStatus());
       assertEquals("test", response.getBody().getData().getUsername());
       verify(userRepository, times(1)).save(any());

   }

    @Test
    void testLoginUser() {
        AuthRequest request = new AuthRequest("test", "1234");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any())).thenReturn("access123");
        when(refreshTokenService.createRefreshToken(any())).thenReturn(refreshToken);

    //    doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("test", "1234"));


        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("SUCCESS", response.getBody().getStatus());
        verify(authenticationManager, times(1)).authenticate(any());
    }

}
