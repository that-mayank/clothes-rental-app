package com.nineleaps.leaps.dto.chatbotDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubQuestionDto {
    private String question;
    private String answer;
}
