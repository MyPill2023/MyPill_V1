package com.mypill.domain.post.entity;

import com.mypill.domain.Image.entity.Image;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.global.base.entitiy.BaseEntity;
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
public class Post extends BaseEntity {
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

    public Long getCommentCnt() {
        long count = 0;
        for (Comment comment : comments) {
            if (comment.getDeleteDate() == null) {
                count++;
            }
        }
        return count;
    }

    public void addImage(Image image) {
        this.image = image;
    }
}
