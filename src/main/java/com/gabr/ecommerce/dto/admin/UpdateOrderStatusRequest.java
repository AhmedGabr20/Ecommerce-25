package com.gabr.ecommerce.dto.admin;


import com.gabr.ecommerce.constant.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class UpdateOrderStatusRequest {
    @NotBlank
    private OrderStatus status;
}

