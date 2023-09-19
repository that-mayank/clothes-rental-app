package com.nineleaps.leaps.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardDto {
    private int totalOrders;
    private double totalEarnings;
}
