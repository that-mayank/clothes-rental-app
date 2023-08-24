package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static com.nineleaps.leaps.config.MessageStrings.TOTAL_INCOME;
import static com.nineleaps.leaps.config.MessageStrings.TOTAL_NUMBER;

@Service
@AllArgsConstructor
@Transactional
public class DashboardServiceImpl implements DashboardServiceInterface {

    private final OrderRepository orderRepository;

    @Override
    public Map<String, Object> dashboardOwnerView(User user) {
        double totalEarnings = 0;
        int totalNumberOfItems = 0;
        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    totalNumberOfItems += 1;
                    totalEarnings += orderItem.getPrice() * orderItem.getQuantity() * (ChronoUnit.DAYS.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate()));
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put(TOTAL_NUMBER, totalNumberOfItems);
        result.put(TOTAL_INCOME, totalEarnings);
        return result;
    }

    @Override
    public Map<YearMonth, Map<String, Object>> analytics(User user) {
        Map<YearMonth, Double> totalEarningsByMonth = new HashMap<>();
        Map<YearMonth, Integer> totalItemsByMonth = new HashMap<>();

        for (Order order : orderRepository.findAll()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct().getUser().equals(user)) {
                    int quantity = orderItem.getQuantity();
                    double price = orderItem.getPrice();
                    LocalDateTime rentalStartDate = orderItem.getRentalStartDate();
                    LocalDateTime rentalEndDate = orderItem.getRentalEndDate();

                    long rentalDurationInDays = ChronoUnit.DAYS.between(rentalStartDate, rentalEndDate);
                    double earnings = price * quantity * rentalDurationInDays;

                    YearMonth month = YearMonth.from(rentalStartDate);

                    totalEarningsByMonth.put(month, totalEarningsByMonth.getOrDefault(month, 0.0) + earnings);
                    totalItemsByMonth.put(month, totalItemsByMonth.getOrDefault(month, 0) + quantity);
                }
            }
        }
        Map<YearMonth, Map<String, Object>> result = new HashMap<>();
        for (Map.Entry<YearMonth, Double> monthEntry : totalEarningsByMonth.entrySet()) {
            YearMonth month = monthEntry.getKey();
            Map<String, Object> monthData = new HashMap<>();
            monthData.put(TOTAL_NUMBER, totalItemsByMonth.get(month));
            monthData.put(TOTAL_INCOME, monthEntry.getValue());
            result.put(month, monthData);
        }
        return result;
    }
}
