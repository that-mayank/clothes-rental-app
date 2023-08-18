package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.User;

import java.time.YearMonth;
import java.util.Map;

public interface DashboardServiceInterface {

    Map<String, Object> dashboardOwnerView(User user);
    Map<YearMonth, Map<String, Object>> analytics(User user);
}
