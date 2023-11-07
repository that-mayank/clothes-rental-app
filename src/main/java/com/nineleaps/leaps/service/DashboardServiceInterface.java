package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.dashboard.DashboardAnalyticsDto;
import com.nineleaps.leaps.dto.dashboard.DashboardDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DashboardServiceInterface {

    DashboardDto dashboardOwnerView(HttpServletRequest request);

    List<DashboardAnalyticsDto> analytics(HttpServletRequest request);
}
