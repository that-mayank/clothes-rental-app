package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.orders.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByRentalEndDateLessThanEqual(LocalDateTime currentDate);
    Page<OrderItem> findByOwnerId(Pageable pageable, Long ownerId);
}
