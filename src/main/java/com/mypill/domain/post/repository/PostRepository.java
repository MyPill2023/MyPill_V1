package com.mypill.domain.post.repository;

import com.mypill.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByDeleteDateIsNullOrderByCreateDateDesc();
}
