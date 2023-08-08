package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.*;
import com.nineleaps.leaps.model.ParentQuestion;
import com.nineleaps.leaps.model.SubQuestionAnswer;
import com.nineleaps.leaps.repository.ParentQuestionRepository;
import com.nineleaps.leaps.repository.SubQuestionAnswerRepository;
import com.nineleaps.leaps.service.QuestionAnswerInterface;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionAnswerService implements QuestionAnswerInterface {


    private ParentQuestionRepository parentQuestionRepository;
    private SubQuestionAnswerRepository subQuestionAnswerRepository;



    @Override
    public String getAnswer(Long subquestionId) {
        SubQuestionAnswer subQuestionAnswer = subQuestionAnswerRepository.findById(subquestionId)
                .orElse(null);
        return (subQuestionAnswer != null) ? subQuestionAnswer.getAnswer() : null;
    }

    @Override
    public WelcomeMessageDto getWelcomeMessageDto(String name) {
        String welcomeMessage = "Hello " + name + " Welcome to Leaps Assistant!";
        String requestMessage = "How may I help you today?";
        List<String> parentQuestions = parentQuestionRepository.findAll()
                .stream()
                .map(ParentQuestion::getParentQuestion)
                .collect(Collectors.toList());
        return new WelcomeMessageDto(welcomeMessage, requestMessage, parentQuestions);
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
    public void addSubQuestionsAndAnswers(Long parentQuestionId, List<SubquestionDto> subquestionDtos) {
        ParentQuestion parentQuestion = parentQuestionRepository.findById(parentQuestionId)
                .orElse(null);

        if (parentQuestion != null) {
            for (SubquestionDto subquestionDto : subquestionDtos) {
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
