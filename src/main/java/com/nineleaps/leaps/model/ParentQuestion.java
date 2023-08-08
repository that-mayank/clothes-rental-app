package com.nineleaps.leaps.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parent_questions")
@Getter
@Setter
public class ParentQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_question")
    private String parentQuestion;

    @OneToMany(mappedBy = "parentQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubQuestionAnswer> subQuestionAnswers = new ArrayList<>();
}
