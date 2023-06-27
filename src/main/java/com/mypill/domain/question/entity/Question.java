package com.mypill.domain.question.entity;

import com.mypill.domain.category.entity.Category;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Builder
@Getter
@Table(name = "questions")
public class Question extends BaseEntity{
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
