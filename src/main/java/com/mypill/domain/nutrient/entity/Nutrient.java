package com.mypill.domain.nutrient.entity;

import com.mypill.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Nutrient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @ManyToMany(mappedBy = "nutrients")
    private List<Product> products = new ArrayList<>();
}
