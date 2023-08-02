package com.mypill.domain.comment.dto.response;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.member.entity.Member;
import lombok.*;

@Data
@Builder
public class CommentResponse {
    private Comment comment;
    private Member commenter;

    public static CommentResponse of(Comment comment, Member commenter){
        return CommentResponse.builder()
                .comment(comment)
                .commenter(commenter)
                .build();
    }
}
