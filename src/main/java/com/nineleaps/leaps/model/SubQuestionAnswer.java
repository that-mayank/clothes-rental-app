package com.nineleaps.leaps.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "sub_question_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubQuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_question_id")
    private ParentQuestion parentQuestion;

    @Column(name = "sub_question")
    private String subQuestion;

    @Column(name = "answer")
    private String answer;

    // Constructors, getters, setters, etc.
}

