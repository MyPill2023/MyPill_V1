package com.mypill.domain.question.service;

import com.mypill.domain.question.dto.QuestionCreateRequest;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.repository.QuestionRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    @Transactional
    public RsData<Question> create(@ModelAttribute QuestionCreateRequest req){
        Question question = req.toEntity();
        questionRepository.save(question);

        return RsData.of("S-1", "질문 등록이 완료되었습니다.", question);
    }
}
