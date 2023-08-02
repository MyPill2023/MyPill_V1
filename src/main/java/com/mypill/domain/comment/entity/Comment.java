package com.mypill.domain.comment.entity;

import com.mypill.domain.post.entity.Post;
import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Comment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    @Column
    private Long commenterId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public void update(String content) {
        this.content = content;
    }
}
