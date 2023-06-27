package com.mypill.domain.question.dto;

import com.mypill.domain.question.entity.Question;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class QuestionCreateRequest {

    private String title;
    private String content;
    private MultipartFile image;

    public Question toEntity() {
        return Question.builder()
                .title(title)
                .content(content)
                .answerCnt(0L)
                .build();
    }
}
