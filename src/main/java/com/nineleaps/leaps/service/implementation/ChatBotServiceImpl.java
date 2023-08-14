package com.nineleaps.leaps.service.implementation;


import com.nineleaps.leaps.dto.chatbotDtos.*;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.chatbot.ParentQuestion;
import com.nineleaps.leaps.model.chatbot.SubQuestionAnswer;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.repository.ParentQuestionRepository;
import com.nineleaps.leaps.repository.SubQuestionAnswerRepository;
import com.nineleaps.leaps.service.ChatBotInterface;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@AllArgsConstructor
public class ChatBotServiceImpl implements ChatBotInterface {


    private ParentQuestionRepository parentQuestionRepository;
    private SubQuestionAnswerRepository subQuestionAnswerRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;



    @Override
    public String getAnswer(Long subquestionId) {
        SubQuestionAnswer subQuestionAnswer = subQuestionAnswerRepository.findById(subquestionId)
                .orElse(null);
        return (subQuestionAnswer != null) ? subQuestionAnswer.getAnswer() : null;
    }

    @Override
    public WelcomeMessageDto getWelcomeMessageDto(User user) {
       String name = user.getFirstName()+user.getLastName();
        String welcomeMessage = "Hello " + name + " Welcome to Leaps Assistant!";
        String requestMessage = "Please select the order for QUERY .";
        List<Order> orders = orderRepository.findByUserOrderByCreateDateDesc(user);
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            OrderDto orderDto = new OrderDto(order);
            orderDtos.add(orderDto);
        }

        return new WelcomeMessageDto(welcomeMessage, requestMessage, orderDtos);
    }

    @Override
    public List<OrderItemDto> getorderItemByOrderId(Long orderId, User user) {
        Optional<Order> orderDto = orderRepository.findByIdAndUserId(orderId, user.getId());
        List<OrderItemDto> orderItemDtos = new ArrayList<>();

        if (orderDto.isPresent()) {
            Order order = orderDto.get();
            for (OrderItem orderItem : order.getOrderItems()) {
                OrderItemDto orderItemDto = new OrderItemDto(orderItem);
                orderItemDtos.add(orderItemDto);
            }
        }

        return orderItemDtos;
    }



    @Override
    public List<ParentQuestionDto> getParentQuestionsForOrderItem(Long orderItemId) {
        List<ParentQuestionDto> parentQuestions = new ArrayList<>();

        // Fetch the order item by orderItemId
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItemId);
        if (orderItemOptional.isPresent()) {
            OrderItem orderItem = orderItemOptional.get();

            // Fetch the product associated with the order item
            Product product = orderItem.getProduct();

            // Fetch all pre-defined parent questions from the repository
            List<ParentQuestion> allParentQuestions = parentQuestionRepository.findAll();

            // Populate ParentQuestionDto objects based on pre-defined parent questions
            for (ParentQuestion parentQuestion : allParentQuestions) {
                ParentQuestionDto parentQuestionDto = new ParentQuestionDto();
                if (parentQuestion.getId() == 1) {
                    String updatedParentQuestion = parentQuestion.getParentQuestion() + " with " + product.getName();
                    parentQuestionDto.setParentQuestion(updatedParentQuestion);
                } else {
                    parentQuestionDto.setParentQuestion(parentQuestion.getParentQuestion());
                }
                parentQuestions.add(parentQuestionDto);
            }

            // You can add more parent questions based on your business logic and product details

            return parentQuestions;
        } else {
            // Handle case when order item is not found
            throw new OrderNotFoundException("Order item not found");
        }
    }




    @Override
    public List<SubQuestionAnswerResponseDto> getSubquestions(Long parentQuestionId) {
        ParentQuestion parentQuestion = parentQuestionRepository.findById(parentQuestionId)
                .orElse(null);
        List<SubQuestionAnswerResponseDto> subQuestionsResponse = new ArrayList<>();

        if (parentQuestion != null) {
            subQuestionsResponse = parentQuestion.getSubQuestionAnswers()
                    .stream()
                    .map(sub -> new SubQuestionAnswerResponseDto(sub.getId(), sub.getSubQuestion(),sub.getAnswer()))
                    .collect(Collectors.toList());
        }

        return subQuestionsResponse;
    }




    @Override
    public void addParentQuestions(List<ParentQuestionDto> parentQuestionDtos) {
        for (ParentQuestionDto parentQuestionDto : parentQuestionDtos) {
            ParentQuestion newParentQuestion = new ParentQuestion();
            newParentQuestion.setParentQuestion(parentQuestionDto.getParentQuestion());
            parentQuestionRepository.save(newParentQuestion);
        }
    }
    @Override
    public List<ParentQuestionResponseDto> getAllParentQuestionsWithSubquestions() {
        List<ParentQuestion> parentQuestions = parentQuestionRepository.findAll();
        List<ParentQuestionResponseDto> parentQuestionsResponse = new ArrayList<>();

        for (ParentQuestion parentQuestion : parentQuestions) {
            List<SubQuestionAnswerResponseDto> subQuestionsResponse = parentQuestion.getSubQuestionAnswers()
                    .stream()
                    .map(sub -> new SubQuestionAnswerResponseDto(sub.getId(), sub.getSubQuestion(), sub.getAnswer()))
                    .collect(Collectors.toList());

            ParentQuestionResponseDto parentQuestionResponse = new ParentQuestionResponseDto(parentQuestion.getId(), parentQuestion.getParentQuestion(), subQuestionsResponse);
            parentQuestionsResponse.add(parentQuestionResponse);
        }

        return parentQuestionsResponse;
    }



    @Override
    public void addSubQuestionsAndAnswers(Long parentQuestionId, List<SubQuestionDto> subquestionDtos) {
        ParentQuestion parentQuestion = parentQuestionRepository.findById(parentQuestionId)
                .orElse(null);

        if (parentQuestion != null) {
            for (SubQuestionDto subquestionDto : subquestionDtos) {
                SubQuestionAnswer subQuestionAnswer = new SubQuestionAnswer();
                subQuestionAnswer.setSubQuestion(subquestionDto.getQuestion());
                subQuestionAnswer.setAnswer(subquestionDto.getAnswer());
                subQuestionAnswer.setParentQuestion(parentQuestion);
                parentQuestion.getSubQuestionAnswers().add(subQuestionAnswer);
            }
            parentQuestionRepository.save(parentQuestion);
        }
    }
}

//        List<String> parentQuestions = parentQuestionRepository.findAll()
//                .stream()
//                .map(ParentQuestion::getParentQuestion)
//                .collect(Collectors.toList());