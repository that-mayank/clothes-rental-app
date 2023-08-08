package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.ChatBotOrderDto;
import com.nineleaps.leaps.dto.WelcomeMessageChatBotDto;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.service.ChatBotInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ChatBotServiceImpl implements ChatBotInterface {

    private OrderRepository orderRepository;

    @Override
    public WelcomeMessageChatBotDto welcomeMessageChatBotDto(User user) {
        String greeting = "Welcome "+user.getFirstName()+user.getLastName();
        String request = "Please select the order for which you seek support.";
        List<Order> orders = orderRepository.findByUserOrderByCreateDateDesc(user);
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            OrderDto orderDto = new OrderDto(order);
            orderDtos.add(orderDto);
        }
        return new WelcomeMessageChatBotDto(greeting,request,orderDtos);

    }

    @Override
    public List<OrderItemDto> getOrderItemsById(Long orderId, User user) {
        Optional<Order> order = orderRepository.findById(orderId);
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        if(order.get().getUser().equals(user)){
            for (OrderItem orderItem : order.get().getOrderItems()) {
                OrderItemDto orderItemDto = new OrderItemDto(orderItem);
                orderItemDtos.add(orderItemDto);
            }
        }
        return orderItemDtos;
    }
}
