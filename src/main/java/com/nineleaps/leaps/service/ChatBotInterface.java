package com.nineleaps.leaps.service;


import com.nineleaps.leaps.dto.chatbotDtos.ParentQuestionDto;
import com.nineleaps.leaps.dto.chatbotDtos.SubQuestionAnswerResponseDto;
import com.nineleaps.leaps.dto.chatbotDtos.SubQuestionDto;
import com.nineleaps.leaps.dto.chatbotDtos.WelcomeMessageDto;
import com.nineleaps.leaps.dto.chatbotDtos.ParentQuestionResponseDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.model.User;


import java.util.List;

public interface ChatBotInterface {


    String getAnswer(Long questionId);

    WelcomeMessageDto getWelcomeMessageDto(User user);

    List<OrderItemDto> getorderItemByOrderId(Long orderId,User user);
    List<SubQuestionAnswerResponseDto> getSubquestions(Long parentQuestionId);


    List<ParentQuestionDto> getParentQuestionsForOrderItem(Long orderItemId);


    void addParentQuestions(List<ParentQuestionDto> parentQuestionDtos);


    void addSubQuestionsAndAnswers(Long parentQuestionId, List<SubQuestionDto> subquestionDtos);

    List<ParentQuestionResponseDto> getAllParentQuestionsWithSubquestions();
}