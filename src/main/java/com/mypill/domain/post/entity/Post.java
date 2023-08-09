package com.mypill.domain.post.entity;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.image.entity.ImageOperator;
import com.mypill.domain.post.dto.request.PostRequest;
import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class Post extends BaseEntity implements ImageOperator {
    private Long posterId;

    @NotNull
    private String title;

    @Column(columnDefinition = "TEXT")
    @NotNull
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    public static Post of(PostRequest postRequest, Long posterId) {
        return Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .posterId(posterId)
                .build();
    }

    public Long getCommentCnt() {
        return comments.stream().filter(comment -> comment.getDeleteDate() == null).count();
    }

    public void update(PostRequest postRequest) {
        this.title = postRequest.getTitle();
        this.content = postRequest.getContent();
    }

    @Override
    public void addImage(Image image) {
        this.image = image;
    }

    @Override
    public String getFolderName() {
        return "post";
    }
}
