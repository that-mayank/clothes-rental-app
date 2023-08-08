package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.*;
import com.nineleaps.leaps.model.ParentQuestion;

import java.util.List;

public interface QuestionAnswerInterface {


    String getAnswer(Long questionId);

    WelcomeMessageDto getWelcomeMessageDto(String name);


    List<SubQuestionAnswerResponseDto> getSubquestions(Long parentQuestionId);




    void addParentQuestions(List<ParentQuestionDto> parentQuestionDtos);


    void addSubQuestionsAndAnswers(Long parentQuestionId, List<SubquestionDto> subquestionDtos);

    List<ParentQuestionResponseDto> getAllParentQuestionsWithSubquestions();
}
