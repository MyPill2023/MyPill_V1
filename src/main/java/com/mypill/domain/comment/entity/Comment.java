package com.mypill.domain.comment.entity;

import com.mypill.domain.post.entity.Post;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
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
public class Comment extends BaseEntity {
    @ManyToOne
    private Post post;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

}
