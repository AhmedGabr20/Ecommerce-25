package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.dto.ChangePasswordRequest;
import com.gabr.ecommerce.dto.UpdateProfileRequest;
import com.gabr.ecommerce.dto.UserAddressRequest;
import com.gabr.ecommerce.dto.UserAddressResponse;
import com.gabr.ecommerce.dto.UserProfileResponse;
import com.gabr.ecommerce.entity.AppUser;
import com.gabr.ecommerce.entity.UserAddress;
import com.gabr.ecommerce.entity.UserProfile;
import com.gabr.ecommerce.repository.UserAddressRepository;
import com.gabr.ecommerce.repository.UserProfileRepository;
import com.gabr.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final UserAddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    // ============================================================
    // GET CURRENT USER PROFILE
    // ============================================================

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(String email) {

        AppUser user = getUserByEmail(email);

        return mapToProfileResponse(user);
    }

    // ============================================================
    // UPDATE PROFILE
    // ============================================================

    @Transactional
    public UserProfileResponse updateProfile(
            String email,
            UpdateProfileRequest request) {

        AppUser user = getUserByEmail(email);

        UserProfile profile = profileRepository
                .findUserProfileByUser_Id(user.getId())
                .orElseGet(() -> {

                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    user.setProfile(newProfile);

                    return newProfile;
                });

        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName().trim());
        }

        if (request.getPhoneNumber() != null) {

            String phone = request.getPhoneNumber().trim();

            profile.setPhone(phone);
            user.setPhoneNumber(phone);
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
            profile.setImage(request.getAvatarUrl().trim());
        }

        profileRepository.save(profile);
        userRepository.save(user);

        return mapToProfileResponse(user);
    }

    // ============================================================
    // CHANGE PASSWORD
    // ============================================================

    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        AppUser user = getUserByEmail(email);

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "Current password does not match");
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

    // ============================================================
    // GET ADDRESSES
    // ============================================================

    @Transactional(readOnly = true)
    public List<UserAddressResponse> getAddresses(String email) {

        AppUser user = getUserByEmail(email);

        return user.getAddresses()
                .stream()
                .map(this::mapToAddressResponse)
                .toList();
    }

    // ============================================================
    // ADD ADDRESS
    // ============================================================

    @Transactional
    public UserAddressResponse addAddress(
            String email,
            UserAddressRequest request) {

        AppUser user = getUserByEmail(email);

        boolean isDefault =
                Boolean.TRUE.equals(request.getIsDefault());

        if (isDefault) {
            unsetDefaultAddresses(user);
        }

        UserAddress address = UserAddress.builder()
                .user(user)
                .addressLine1(request.getAddressLine1().trim())
                .addressLine2(trimToNull(request.getAddressLine2()))
                .city(request.getCity().trim())
                .state(trimToNull(request.getState()))
                .postalCode(trimToNull(request.getPostalCode()))
                .country(request.getCountry().trim())
                .isDefault(isDefault)
                .build();

        UserAddress savedAddress =
                addressRepository.save(address);

        user.getAddresses().add(savedAddress);

        return mapToAddressResponse(savedAddress);
    }

    // ============================================================
    // UPDATE ADDRESS
    // ============================================================

    @Transactional
    public UserAddressResponse updateAddress(
            String email,
            Long addressId,
            UserAddressRequest request) {

        AppUser user = getUserByEmail(email);

        UserAddress address =
                getUserAddress(user, addressId);

        boolean isDefault =
                Boolean.TRUE.equals(request.getIsDefault());

        if (isDefault) {
            unsetDefaultAddresses(user);
        }

        address.setAddressLine1(
                request.getAddressLine1().trim());

        address.setAddressLine2(
                trimToNull(request.getAddressLine2()));

        address.setCity(
                request.getCity().trim());

        address.setState(
                trimToNull(request.getState()));

        address.setPostalCode(
                trimToNull(request.getPostalCode()));

        address.setCountry(
                request.getCountry().trim());

        address.setIsDefault(isDefault);

        UserAddress savedAddress =
                addressRepository.save(address);

        return mapToAddressResponse(savedAddress);
    }

    // ============================================================
    // DELETE ADDRESS
    // ============================================================

    @Transactional
    public void deleteAddress(
            String email,
            Long addressId) {

        AppUser user = getUserByEmail(email);

        UserAddress address =
                getUserAddress(user, addressId);

        boolean wasDefault =
                Boolean.TRUE.equals(address.getIsDefault());

        user.getAddresses().remove(address);

        addressRepository.delete(address);

        /*
         * If the deleted address was the default address,
         * make another address default.
         */
        if (wasDefault && !user.getAddresses().isEmpty()) {

            UserAddress newDefault =
                    user.getAddresses().get(0);

            newDefault.setIsDefault(true);

            addressRepository.save(newDefault);
        }
    }

    // ============================================================
    // SET DEFAULT ADDRESS
    // ============================================================

    @Transactional
    public UserAddressResponse setDefaultAddress(
            String email,
            Long addressId) {

        AppUser user = getUserByEmail(email);

        UserAddress address =
                getUserAddress(user, addressId);

        unsetDefaultAddresses(user);

        address.setIsDefault(true);

        UserAddress savedAddress =
                addressRepository.save(address);

        return mapToAddressResponse(savedAddress);
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private AppUser getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private UserAddress getUserAddress(
            AppUser user,
            Long addressId) {

        UserAddress address =
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "Address does not belong to current user");
        }

        return address;
    }

    private void unsetDefaultAddresses(AppUser user) {

        user.getAddresses()
                .forEach(address ->
                        address.setIsDefault(false));
    }

    private String trimToNull(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    // ============================================================
    // PROFILE MAPPER
    // ============================================================

    private UserProfileResponse mapToProfileResponse(
            AppUser user) {

        UserProfile profile =
                user.getProfile();

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())

                .firstName(
                        profile != null
                                ? profile.getFirstName()
                                : null)

                .lastName(
                        profile != null
                                ? profile.getLastName()
                                : null)

                .phoneNumber(
                        user.getPhoneNumber())

                .avatarUrl(
                        user.getAvatarUrl())

                .isActive(
                        user.getIsActive())

                .roles(
                        user.getRoles()
                                .stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toSet())
                )

                .permissions(
                        user.getRoles()
                                .stream()
                                .flatMap(role ->
                                        role.getPermissions()
                                                .stream())
                                .map(permission ->
                                        permission.getName())
                                .collect(Collectors.toSet())
                )

                .addresses(
                        user.getAddresses()
                                .stream()
                                .map(this::mapToAddressResponse)
                                .toList()
                )

                .build();
    }

    // ============================================================
    // ADDRESS MAPPER
    // ============================================================

    private UserAddressResponse mapToAddressResponse(
            UserAddress address) {

        UserAddressResponse response =
                new UserAddressResponse();

        response.setId(address.getId());
        response.setAddressLine1(
                address.getAddressLine1());
        response.setAddressLine2(
                address.getAddressLine2());
        response.setCity(
                address.getCity());
        response.setState(
                address.getState());
        response.setPostalCode(
                address.getPostalCode());
        response.setCountry(
                address.getCountry());
        response.setIsDefault(
                address.getIsDefault());

        return response;
    }
}