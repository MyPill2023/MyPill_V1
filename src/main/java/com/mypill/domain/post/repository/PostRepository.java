package com.mypill.domain.post.repository;

import com.mypill.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findByDeleteDateIsNullOrderByCreateDateDesc();

    List<Post> findByPosterIdAndDeleteDateIsNullOrderByIdDesc(Long posterId);

    Optional<Post> findByIdAndDeleteDateIsNull(Long postId);

    List<Post> findByDeleteDateIsNotNull();

    List<Post> findByPosterId(Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete from Post p where p.deleteDate != null ")
    void hardDelete();
}
