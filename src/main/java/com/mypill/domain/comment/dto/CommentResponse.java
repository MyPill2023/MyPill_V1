package com.mypill.domain.post.dto;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.entity.Post;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentResponse {
    private Comment comment;
    private Member commenter;
}
