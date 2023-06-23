package com.mypill.domain.survey.repository;

import com.mypill.domain.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey,Long> {
}
