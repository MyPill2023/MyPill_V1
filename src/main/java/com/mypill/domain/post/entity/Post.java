package com.mypill.domain.post.entity;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.image.entity.ImageOperator;
import com.mypill.domain.post.dto.PostRequest;
import com.mypill.global.base.entity.BaseEntity;
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
public class Post extends BaseEntity implements ImageOperator {
    @Column
    private Long posterId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    private Image image;

    public Post(PostRequest postRequest, Long id, List<Comment> comments) {
        this.title = postRequest.getTitle();
        this.content = postRequest.getContent();
        this.posterId = id;
        this.comments = comments;
    }

    public Long getCommentCnt() {
        long count = 0;
        for (Comment comment : comments) {
            if (comment.getDeleteDate() == null) {
                count++;
            }
        }
        return count;
    }

    public void update(PostRequest postRequest) {
        this.title = postRequest.getTitle();
        this.content = postRequest.getContent();
    }

    public void addImage(Image image) {
        this.image = image;
    }

    @Override
    public String getFolderName() {
        return "post";
    }
}
