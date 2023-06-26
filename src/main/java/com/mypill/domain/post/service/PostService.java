package com.mypill.domain.post.service;

import com.mypill.domain.post.dto.PostCreateRequest;
import com.mypill.domain.post.dto.PostResponse;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public List<Post> getList() {
        return postRepository.findAllByOrderByCreateDateDesc();
    }

    @Transactional
    public RsData<Post> create(PostCreateRequest postCreateRequest) {
        Post post = postCreateRequest.toEntity();
        postRepository.save(post);

        return RsData.of("S-1", "질문 등록이 완료되었습니다.", post);
    }

    public RsData<PostResponse> get(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        return RsData.of("S-1", "존재하는 상품입니다.", PostResponse.of(post));
    }
}
