package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    List<Order> findByUserOrderByCreateDateDesc(User user);
}
