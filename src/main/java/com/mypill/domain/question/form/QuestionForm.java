package com.mypill.domain.question.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class QuestionForm {
    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max = 200)
    private String title;
    @NotEmpty(message = "모임소개는 필수항목입니다.")
    private String content;
}
