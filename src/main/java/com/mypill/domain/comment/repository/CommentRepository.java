package com.mypill.domain.comment.repository;

import com.mypill.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    List<Comment> findByCommenterIdAndDeleteDateIsNullOrderByIdDesc(Long commenterId);

    List<Comment> findByCommenterId(Long id);

    Optional<Comment> findByIdAndDeleteDateIsNull(Long commentId);

    @Modifying
    @Query("delete from Comment c where c.deleteDate != null")
    void hardDelete();
}
