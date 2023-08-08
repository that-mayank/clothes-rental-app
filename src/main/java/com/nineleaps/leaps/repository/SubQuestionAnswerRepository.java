package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.SubQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubQuestionAnswerRepository extends JpaRepository<SubQuestionAnswer, Long> {
}
