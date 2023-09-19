package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.dashboard.DashboardAnalyticsDto;
import com.nineleaps.leaps.dto.dashboard.DashboardDto;
import com.nineleaps.leaps.model.User;

import java.util.List;

public interface DashboardServiceInterface {

    DashboardDto dashboardOwnerView(User user);
    List<DashboardAnalyticsDto> analytics(User user);
}
