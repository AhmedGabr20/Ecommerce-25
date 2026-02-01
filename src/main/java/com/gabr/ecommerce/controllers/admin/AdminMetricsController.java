package com.gabr.ecommerce.controllers.admin;

import com.gabr.ecommerce.dto.ApiResponse;
import com.gabr.ecommerce.dto.admin.AdminSummaryDto;
import com.gabr.ecommerce.dto.admin.RevenuePointDto;
import com.gabr.ecommerce.dto.admin.TopProductDto;
import com.gabr.ecommerce.service.AdminMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/metrics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMetricsController {
    private final AdminMetricsService adminMetricsService;

    @GetMapping("/summary")
    public ApiResponse<AdminSummaryDto> summary() {
        return ApiResponse.success("OK", adminMetricsService.summary());
    }

    @GetMapping("/revenue")
    public ApiResponse<List<RevenuePointDto>> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success("OK",adminMetricsService.revenue(from,to));
    }

    @GetMapping("/top-products")
    public ApiResponse<List<TopProductDto>> topProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to ,
            @RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success("OK",adminMetricsService.topProducts(from,to,limit));
    }
}
