package com.mypill.domain.nutrient.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "nutrients")
public class Nutrient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nutrient_id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name="description", nullable = false, columnDefinition = "TEXT")
    private String description;


}
