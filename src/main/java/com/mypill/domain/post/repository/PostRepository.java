package com.mypill.domain.post.repository;

import com.mypill.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findByDeleteDateIsNullOrderByCreateDateDesc();

    List<Post> findByPosterIdAndDeleteDateIsNull(Long posterId);

    Optional<Post> findByIdAndDeleteDateIsNull(Long postId);
}
