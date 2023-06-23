package com.mypill.domain.question.service;

import com.mypill.domain.product.dto.response.ProductResponse;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.question.entity.Question;
import com.mypill.domain.question.form.QuestionForm;
import com.mypill.domain.question.repository.QuestionRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    @Transactional
    public RsData<Question> create(QuestionForm questionForm){
        Question question = Question.create(questionForm);
        questionRepository.save(question);

        return RsData.of("S-1", "상품 등록이 완료되었습니다.", question);
    }
}
