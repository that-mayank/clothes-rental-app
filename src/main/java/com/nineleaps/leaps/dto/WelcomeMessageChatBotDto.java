package com.nineleaps.leaps.dto;

import com.nineleaps.leaps.dto.orders.OrderDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WelcomeMessageChatBotDto {
    private String greeeting;
    private String request;
    private List<OrderDto> orderDtoList;
}
