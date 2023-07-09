package com.mypill.domain.comment.repository;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    List<Comment> findByCommenterIdAndDeleteDateIsNullOrderByIdDesc(Long commenterId);

    List<Comment> findByDeleteDateIsNotNull();
}
