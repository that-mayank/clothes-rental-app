package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.dashboard.DashboardAnalyticsDto;
import com.nineleaps.leaps.dto.dashboard.DashboardDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional
@Slf4j // Add SLF4J annotation for logging
public class DashboardServiceImpl implements DashboardServiceInterface {

    private final OrderItemRepository orderItemRepository;
    private final Helper helper;

    @Override
    public DashboardDto dashboardOwnerView(HttpServletRequest request) {
        User user = helper.getUserFromToken(request);
        log.info("Generating dashboard view for owner user: {}", user.getEmail());

        double totalEarnings = 0;
        int totalNumberOfItems = orderItemRepository.findByOwnerId(user.getId()).size();

        for (OrderItem orderItem : orderItemRepository.findByOwnerId(user.getId())) {
            totalEarnings += orderItem.getPrice() * orderItem.getQuantity() * (ChronoUnit.DAYS.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate()));
        }

        log.info("Dashboard view generated for owner user: {}. Total earnings: {}, Total number of items: {}", user.getEmail(), totalEarnings, totalNumberOfItems);

        return new DashboardDto(totalNumberOfItems, totalEarnings);
    }

    @Override
    public List<DashboardAnalyticsDto> analytics(HttpServletRequest request) {
        User user = helper.getUserFromToken(request);
        log.info("Generating analytics data for owner user: {}", user.getEmail());

        Map<YearMonth, Double> totalEarningsByMonth = new HashMap<>();
        Map<YearMonth, Integer> totalItemsByMonth = new HashMap();

        for (OrderItem orderItem : orderItemRepository.findByOwnerId(user.getId())) {
            int quantity = orderItem.getQuantity();
            double price = orderItem.getPrice();

            long rentalDurationInDays = ChronoUnit.DAYS.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate());
            double earnings = price * quantity * rentalDurationInDays;

            YearMonth month = YearMonth.from(orderItem.getCreatedDate());

            totalEarningsByMonth.put(month, totalEarningsByMonth.getOrDefault(month, 0.0) + earnings);
            totalItemsByMonth.put(month, totalItemsByMonth.getOrDefault(month, 0) + quantity);
        }

        List<DashboardAnalyticsDto> result = new ArrayList<>();

        for (Map.Entry<YearMonth, Double> monthEntry : totalEarningsByMonth.entrySet()) {
            YearMonth month = monthEntry.getKey();
            int totalOrders = totalItemsByMonth.get(month);
            double totalEarning = monthEntry.getValue();
            result.add(new DashboardAnalyticsDto(month, totalOrders, totalEarning));
        }

        log.info("Generated analytics data for owner user: {}", user.getEmail());

        return result;
    }
}
