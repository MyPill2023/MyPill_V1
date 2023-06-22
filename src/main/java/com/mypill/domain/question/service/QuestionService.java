package com.mypill.domain.question.service;

import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository boarRepository;

    public List<Question> getList() {
        return boarRepository.findAll();
    }
}
