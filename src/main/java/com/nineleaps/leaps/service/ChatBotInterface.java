package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.WelcomeMessageChatBotDto;

import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.model.User;

import java.util.List;

public interface ChatBotInterface {
    WelcomeMessageChatBotDto welcomeMessageChatBotDto(User user);

    List<OrderItemDto> getOrderItemsById(Long orderId, User user);
}
