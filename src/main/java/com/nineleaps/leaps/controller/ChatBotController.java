package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.*;
import com.nineleaps.leaps.dto.chatbotDtos.*;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.ChatBotInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/chatbot")
@AllArgsConstructor
public class ChatBotController {

    private ChatBotInterface questionAnswerService;
    private Helper helper;


    @GetMapping("/welcomemessage")
    public WelcomeMessageDto getWelcomeMessage(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        return questionAnswerService.getWelcomeMessageDto(user);
    }
    @GetMapping("/getorderitemsbyorderid")
    public List<OrderItemDto> getOrderItemsByOrderId(HttpServletRequest request,
                                                                     @RequestParam("orderId") Long orderId) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        return questionAnswerService.getorderItemByOrderId(orderId, user);
    }




    @GetMapping("/getsubquestions")
    public List<SubQuestionAnswerResponseDto> getSubquestions(@RequestParam("parentQuestionId") Long parentQuestionId) {
        return questionAnswerService.getSubquestions(parentQuestionId);
    }

    @GetMapping("/getanswer")
    public String getAnswer(@RequestParam("subquestionId") Long subquestionId) {
        return questionAnswerService.getAnswer(subquestionId);
    }



    @PostMapping("/addparentquestions")
    public void addParentQuestions(@RequestBody List<ParentQuestionDto> parentQuestionDtos) {
        questionAnswerService.addParentQuestions(parentQuestionDtos);
    }


    @PostMapping("/addsubquestionsanswers")
    public void addSubQuestionsAndAnswers(@RequestParam("parentQuestionId") Long parentQuestionId,
                                          @RequestBody List<SubQuestionDto> subquestionDtos) {
        questionAnswerService.addSubQuestionsAndAnswers(parentQuestionId, subquestionDtos);
    }

    @GetMapping("/getparentquestionsfororderitem")
    public List<ParentQuestionDto> getParentQuestionsForOrderItem(
            @RequestParam("orderItemId") Long orderItemId) {
        List<ParentQuestionDto> parentQuestions = questionAnswerService.getParentQuestionsForOrderItem(orderItemId);
        return parentQuestions;
    }
}