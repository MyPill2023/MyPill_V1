package com.mypill.domain.comment.entity;

import com.mypill.domain.comment.dto.request.CommentRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.entity.Post;
import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    private Long commenterId;
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    public static Comment createComment(Post post,
                                Member commenter,
                                CommentRequest commentRequest){
        return Comment.builder()
                .post(post)
                .commenterId(commenter.getId())
                .content(commentRequest.getNewContent())
                .build();
    }

    public void update(String content) {
        this.content = content;
    }
}
