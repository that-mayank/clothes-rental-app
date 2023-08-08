package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.WelcomeMessageChatBotDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.implementation.ChatBotServiceImpl;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/chatbot")
@AllArgsConstructor
public class ChatBotController {
    private ChatBotServiceImpl chatBotService;
    private Helper helper;

    @GetMapping("/getwelcomemessage")
    public WelcomeMessageChatBotDto welcomeMessageChatBotDto(HttpServletRequest request){
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        return chatBotService.welcomeMessageChatBotDto(user);
    }


}
