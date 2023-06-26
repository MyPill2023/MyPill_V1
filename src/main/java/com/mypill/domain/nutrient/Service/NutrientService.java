package com.mypill.domain.nutrient.Service;

import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.nutrient.repository.NutrientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NutrientService {
    private final NutrientRepository nutrientRepository;
    public List<Nutrient> findAll() {
        return nutrientRepository.findAll();
    }

    public List<Nutrient> findAllByOrderByNameAsc() {
        return nutrientRepository.findAllByOrderByNameAsc();
    }

    public List<Nutrient> findByIdIn(List<Long> nutrientIds) {
        return nutrientRepository.findByIdIn(nutrientIds);
    }
}
