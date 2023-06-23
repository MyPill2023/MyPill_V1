package com.mypill.domain.question.dto;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.question.entity.Question;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class QuestionCreateRequest {

    private String title;
    private String content;
    private MultipartFile uploadImage;

    public Question toEntity(Member member) {
        return Question.builder()
                .member(member)
                .title(title)
                .content(content)
                .answerCnt(0L)
                .build();
    }
}
