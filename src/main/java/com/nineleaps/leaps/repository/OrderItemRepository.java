package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.orders.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
        //List<OrderItem> findByRentalEndDateLessThanEqualAndRentalEndDateGreaterThanEqual(LocalDateTime endDate, LocalDateTime startDate);
        List<OrderItem> findByRentalEndDateLessThanEqual(LocalDateTime currentDate);
}
