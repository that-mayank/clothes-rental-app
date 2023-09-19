package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.dashboard.DashboardAnalyticsDto;
import com.nineleaps.leaps.dto.dashboard.DashboardDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
public class DashboardServiceImpl implements DashboardServiceInterface {

    private final OrderItemRepository orderItemRepository;

    @Override
    public DashboardDto dashboardOwnerView(User user) {
        double totalEarnings = 0;
        int totalNumberOfItems = orderItemRepository.findByOwnerId(user.getId()).size();

        for (OrderItem orderItem : orderItemRepository.findByOwnerId(user.getId())) {
            totalEarnings += orderItem.getPrice() * orderItem.getQuantity() * (ChronoUnit.DAYS.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate()));
        }

        return new DashboardDto(totalNumberOfItems, totalEarnings);
    }

    @Override
    public List<DashboardAnalyticsDto> analytics(User user) {
        Map<YearMonth, Double> totalEarningsByMonth = new HashMap<>();
        Map<YearMonth, Integer> totalItemsByMonth = new HashMap<>();

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

        return result;
    }
}
