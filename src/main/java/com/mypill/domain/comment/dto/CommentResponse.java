package com.mypill.domain.comment.dto;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentResponse {
    private Comment comment;
    private Member commenter;

    public CommentResponse of(Comment comment, Member commenter){
        return CommentResponse.builder()
                .comment(comment)
                .commenter(commenter)
                .build();
    }
}
