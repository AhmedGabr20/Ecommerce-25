package com.gabr.ecommerce.service;

import com.gabr.ecommerce.dto.admin.AdminSummaryDto;
import com.gabr.ecommerce.dto.admin.RevenuePointDto;
import com.gabr.ecommerce.dto.admin.TopProductDto;

import java.time.LocalDate;
import java.util.List;

public interface AdminMetricsService {
    AdminSummaryDto summary();
    List<RevenuePointDto> revenue(LocalDate from, LocalDate to);
    List<TopProductDto> topProducts(LocalDate from, LocalDate to, int limit);
}
