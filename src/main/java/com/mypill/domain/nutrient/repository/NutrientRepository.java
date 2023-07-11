package com.mypill.domain.nutrient.repository;

import com.mypill.domain.nutrient.entity.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NutrientRepository extends JpaRepository<Nutrient, Long> {
    List<Nutrient> findAllByOrderByNameAsc();

    List<Nutrient> findByIdIn(List<Long> nutrientIds);

    Optional<Nutrient> findById(Long nutrientId);
}
