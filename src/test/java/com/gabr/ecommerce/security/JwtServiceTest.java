package com.gabr.ecommerce.security;

import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private JwtService jwtService;
    private AppUser testUser;
    private UserDetails userDetails;


    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        testUser = new AppUser().builder()
                .username("test")
                .password("1234")
                .role(Role.USER)
                .build();
        userDetails = User.builder()
                .username("test")
                .password("123")
                .roles(Role.USER.toString())
                .build();
    }

    @Test
    void testGenerateAccessToken_NotNull() {
        String token = jwtService.generateAccessToken(testUser);
        assertNotNull(token);
    }
    @Test
    void testGenerateRefreshToken_NotNull() {
        String token = jwtService.generateRefreshToken(testUser);
        assertNotNull(token);
    }
    @Test
    void testExtractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateAccessToken(testUser);
        assertEquals("test", jwtService.extractUsername(token));
    }
    @Test
    void testIsTokenValid_ShouldReturnTrueForValidToken() {
        String token = jwtService.generateAccessToken(testUser);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
    @Test
    void testIsTokenValid_ShouldReturnFalseForDifferentUser() {
        String token = jwtService.generateAccessToken(testUser);

        UserDetails anotherUser = User.builder()
                .username("mohamed")
                .password("pass")
                .roles(Role.USER.name())
                .build();

        boolean isValid = jwtService.isTokenValid(token, anotherUser);
        assertFalse(isValid);
    }
    @Test
    void testIsTokenValid_ShouldReturnFalseForExpiredToken() throws InterruptedException {
        // نعمل توكن مؤقت بصلاحية قصيرة جدًا للاختبار
        String shortLivedToken = Jwts.builder()
                .setSubject("test")
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 500)) // نصف ثانية
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                                "SuperSecretKeyForJwtGeneration1234567890".getBytes()),
                        io.jsonwebtoken.SignatureAlgorithm.HS256
                ).compact();

        Thread.sleep(1000); // ننتظر انتهاء الصلاحية

    //    assertFalse(jwtService.isTokenValid(shortLivedToken, userDetails));
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () ->
                jwtService.isTokenValid(shortLivedToken, userDetails)
        );
    }











    // Helper method to extract claims for assertion
    private Claims extractClaims(String token) {
        try {
            var method = JwtService.class.getDeclaredMethod("getClaims", String.class);
            method.setAccessible(true);
            return (Claims) method.invoke(jwtService, token);
        } catch (Exception e) {
            fail("Failed to extract claims: " + e.getMessage());
            return null;
        }
    }

}
