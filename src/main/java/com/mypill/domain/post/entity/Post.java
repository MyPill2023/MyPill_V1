package com.mypill.domain.post.entity;

import com.mypill.domain.member.entity.Member;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Builder
public class Post extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member poster;
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

//    @OneToMany(mappedBy = "post", orphanRemoval = true)
//    private List<Answer> answers; // 답변

    private Long answerCnt;     // 답변 수
}
