package com.mypill.domain.category.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mypill.domain.question.entity.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "category", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question question) {
        questions.add(question);
        question.setCategory(this);
    }


}