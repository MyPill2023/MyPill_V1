package com.mypill.domain.question.entity;

import com.mypill.domain.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;


@ToString
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;


}
