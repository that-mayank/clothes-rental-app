package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.chatbot.ParentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentQuestionRepository extends JpaRepository<ParentQuestion, Long> {
}