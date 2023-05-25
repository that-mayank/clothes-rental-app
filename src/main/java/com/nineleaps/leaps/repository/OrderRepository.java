package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
//    List<Order> findByUser(User user);
    List<Order> findByUserOrderByCreateDateDesc(User user);
}
