package com.mypill.domain.comment.repository;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByWriter(Member writer);
}
