package com.gabr.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserAddressRequest {

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    private String state;
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country = "Egypt";

    private Boolean isDefault = false;
}
