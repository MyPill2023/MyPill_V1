package com.mypill.domain.question.dto.response;

import com.mypill.domain.question.entity.Question;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {

    private Long id;
    private String content;

    public static QuestionResponse of(Question question){
        return QuestionResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .build();
    }
}
