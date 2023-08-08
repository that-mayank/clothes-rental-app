package com.nineleaps.leaps.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ParentQuestionResponseDto {
    private Long id;
    private String parentQuestion;
    private List<SubQuestionAnswerResponseDto> subQuestions;
}
