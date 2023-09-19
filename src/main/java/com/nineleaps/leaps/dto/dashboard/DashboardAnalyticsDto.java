package com.nineleaps.leaps.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
public class DashboardAnalyticsDto {
    private YearMonth month;
    private int totalOrders;
    private double totalEarnings;

}
