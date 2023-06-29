package com.mypill.domain.post.repository;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.entity.Post;
import com.mypill.global.rsData.RsData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByDeleteDateIsNullOrderByCreateDateDesc();

    List<Post> findByPoster(Member poster);

    List<Post> findByTitleContaining(String keyword);

    List<Post> findByContentContaining(String keyword);
}
