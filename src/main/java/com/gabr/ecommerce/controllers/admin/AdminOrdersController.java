package com.gabr.ecommerce.controllers.admin;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.admin.AdminOrderDto;
import com.gabr.ecommerce.dto.admin.UpdateOrderStatusRequest;
import com.gabr.ecommerce.service.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
public class AdminOrdersController {

    private final AdminOrderService adminOrderService;


    @GetMapping
    public ApiResponse<Page<AdminOrderDto>> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        return ApiResponse.success("OK",
                adminOrderService.list(status, username, page, size, sortBy, dir)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminOrderDto> detail(@PathVariable Long id) {
        return ApiResponse.success("OK",adminOrderService.details(id));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<AdminOrderDto> updateStatus(@PathVariable Long id,@RequestBody UpdateOrderStatusRequest req) {
        return ApiResponse.success("OK",adminOrderService.updateStatus(id, req.getStatus()));
    }



}
