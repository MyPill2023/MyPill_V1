package com.mypill.domain.nutrient.repository;

import com.mypill.domain.nutrient.entity.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NutrientRepository extends JpaRepository<Nutrient, Long> {
    public List<Nutrient> findAllByOrderByNameAsc();
}
