package com.mypill.domain.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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

    public Question Question (Question question) {
        Category category = new Category();
        return Question.builder()
                .category(category)
                .build();
    }

    @ManyToMany(mappedBy = "categories")
    private List<Product> products = new ArrayList<>();
}