package com.nineleaps.leaps.dto.chatbotDtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class SubQuestionAnswerResponseDto {
    private Long id;
    private String subquestion;
    private String answer;

    public SubQuestionAnswerResponseDto(Long id, String subQuestion) {
        this.id = id;
        this.subquestion = subQuestion;
    }
}