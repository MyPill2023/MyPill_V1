package com.mypill.domain.question.repository;

import com.mypill.domain.question.entity.NutrientQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NutrientQuestionRepository extends JpaRepository<NutrientQuestion, Long> {
    List<NutrientQuestion> findByNutrientId(Long questionId);

    List<NutrientQuestion> findByQuestionId(Long questionId);
}
