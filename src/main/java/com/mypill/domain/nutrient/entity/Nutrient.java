package com.mypill.domain.nutrient.entity;

import com.mypill.domain.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "nutrients")
public class Nutrient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @Column(columnDefinition = "TEXT")
    @NotNull
    private String description;
    @ManyToMany(mappedBy = "nutrients")
    private List<Product> products = new ArrayList<>();
}
