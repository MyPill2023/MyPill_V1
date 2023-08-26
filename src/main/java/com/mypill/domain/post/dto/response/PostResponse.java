package com.mypill.domain.post.dto.response;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.entity.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private String imageFilePath;
    private Long commentCount;
    private Long posterId;
    private String posterName;

    public static PostResponse of(Post post, Member poster){
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createDate(post.getCreateDate())
                .imageFilePath(Optional.ofNullable(post.getImage()).map(Image::getFilepath).orElse("/image-null"))
                .commentCount(post.getCommentCnt())
                .posterId(poster.getId())
                .posterName(poster.getName())
                .build();
    }
}
