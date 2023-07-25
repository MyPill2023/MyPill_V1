package com.mypill.domain.category.entity;

import com.mypill.domain.product.entity.Product;
import com.mypill.domain.question.entity.Question;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "category", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}