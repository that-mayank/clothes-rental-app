package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.*;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.QuestionAnswerInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/question-answer")
@AllArgsConstructor
public class QuestionAnswerController {

    private QuestionAnswerInterface questionAnswerService;
    private Helper helper;


    @GetMapping("/welcomemessage")
    public WelcomeMessageDto getWelcomeMessage(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        return questionAnswerService.getWelcomeMessageDto(user.getFirstName()+user.getLastName());
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
                                          @RequestBody List<SubquestionDto> subquestionDtos) {
        questionAnswerService.addSubQuestionsAndAnswers(parentQuestionId, subquestionDtos);
    }

    @GetMapping("/getallparentquestions")
    public ResponseEntity<List<ParentQuestionResponseDto>> getAllParentQuestionsWithSubquestions() {
        List<ParentQuestionResponseDto> parentQuestions = questionAnswerService.getAllParentQuestionsWithSubquestions();
        return ResponseEntity.ok(parentQuestions);
    }
}
