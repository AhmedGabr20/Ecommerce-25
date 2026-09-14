package com.gabr.ecommerce.controllers;

import com.gabr.ecommerce.dto.ChangePasswordRequest;
import com.gabr.ecommerce.dto.UpdateProfileRequest;
import com.gabr.ecommerce.dto.UserAddressRequest;
import com.gabr.ecommerce.dto.UserAddressResponse;
import com.gabr.ecommerce.dto.UserProfileResponse;
import com.gabr.ecommerce.service.impl.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // ============================================================
    // PROFILE ENDPOINTS
    // ============================================================

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                accountService.getCurrentUserProfile(userDetails.getUsername())
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        return ResponseEntity.ok(
                accountService.updateProfile(userDetails.getUsername(), request)
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {

        accountService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    // ============================================================
    // ADDRESSES ENDPOINTS
    // ============================================================

    @GetMapping("/addresses")
    public ResponseEntity<List<UserAddressResponse>> getAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                accountService.getAddresses(userDetails.getUsername())
        );
    }

    @PostMapping("/addresses")
    public ResponseEntity<UserAddressResponse> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserAddressRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.addAddress(userDetails.getUsername(), request));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<UserAddressResponse> updateAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long addressId,
            @Valid @RequestBody UserAddressRequest request) {

        return ResponseEntity.ok(
                accountService.updateAddress(userDetails.getUsername(), addressId, request)
        );
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long addressId) {

        accountService.deleteAddress(userDetails.getUsername(), addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/addresses/{id}/default")
    public ResponseEntity<UserAddressResponse> setDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long addressId) {

        return ResponseEntity.ok(
                accountService.setDefaultAddress(userDetails.getUsername(), addressId)
        );
    }
}