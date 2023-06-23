package com.mypill.domain.question.entity;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.question.form.QuestionForm;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Question extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;      // 작성자

    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
//    @OneToMany(mappedBy = "question", orphanRemoval = true)
//    private List<Answer> answers; // 답변
    private Long answerCnt;     // 답변 수

//    @OneToOne(fetch = FetchType.LAZY)
//    private UploadImage uploadImage; // 이미지

    public void update(QuestionDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

//    public void answerChange(Long answerCnt) {
//        this.answerCnt = answerCnt;
//    }
//
//    public void setUploadImage(UploadImage uploadImage) {
//        this.uploadImage = uploadImage;
//    }
}
