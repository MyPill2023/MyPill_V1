package com.mypill.domain.post.entity;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private List<Comment> comments;

    public Long getAnswerCnt() {
        return (long) this.comments.size();
    }
}
