package com.mypill.domain.comment.dto.response;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private Long commentId;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private String commenterName;
    private Long commenterId;
    private Long postId;

    public static CommentResponse of(Comment comment, Member commenter) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .createDate(comment.getCreateDate())
                .modifyDate(comment.getModifyDate())
                .commenterName(commenter.getName())
                .commenterId(commenter.getId())
                .build();
    }

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .createDate(comment.getCreateDate())
                .build();
    }
}
