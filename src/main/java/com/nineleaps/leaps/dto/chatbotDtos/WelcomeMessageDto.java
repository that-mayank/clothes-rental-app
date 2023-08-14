package com.nineleaps.leaps.dto.chatbotDtos;

import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.model.orders.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WelcomeMessageDto {
    private String message1;
    private String message2;
    private List<OrderDto> orders;
}