package com.gabr.ecommerce.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminSummaryDto {
    private long totalUsers;
    private long totalOrders;
    private long paidOrders;
    private long newOrders;
    private long shippedOrders;
    private long completedOrders;
    private long canceledOrders;

    private double totalRevenue;
    private double avgOrderValue;
}
