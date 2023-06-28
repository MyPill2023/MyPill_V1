package com.mypill.domain.question.service;

import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> findByCategoryId(Long categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }

}
