package com.nineleaps.leaps.dto;

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
    private List<String> questions;
}
