package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findAllByUserOrderByCreateDateDesc(User user);

    Cart findByUserIdAndProductId(Long userId, Long productId);

    List<Cart> deleteByUser(User user);
}
