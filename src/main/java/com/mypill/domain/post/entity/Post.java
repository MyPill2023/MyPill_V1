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

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Post extends BaseEntity {

    @ManyToOne
    private Member poster;
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "post")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public Long getCommentCnt() {
        return (long) this.comments.size();
    }

    public List<Comment> getAvailableComments() {
        return this.comments.stream()
                .filter(x -> x.getDeleteDate() == null)
                .toList();
    }
}
