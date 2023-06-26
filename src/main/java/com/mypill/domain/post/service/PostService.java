package com.mypill.domain.post.service;

import com.mypill.domain.post.dto.PostRequest;
import com.mypill.domain.post.dto.PostResponse;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public List<Post> getList() {
        return postRepository.findAllByOrderByCreateDateDesc();
    }

    @Transactional
    public RsData<Post> create(PostRequest postRequest) {
        Post post = postRequest.toEntity();
        postRepository.save(post);

        return RsData.of("S-1", "질문 등록이 완료되었습니다.", post);
    }

    public RsData<PostResponse> get(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        return RsData.of("S-1", "존재하는 게시글입니다.", PostResponse.of(post));
    }

    @Transactional
    public RsData<Post> update(Long postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        post = post.toBuilder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .answerCnt(post.getAnswerCnt())
                .build();
        postRepository.save(post);
        return RsData.of("S-1", "게시글이 수정되었습니다.", post);
    }

    @Transactional
    public RsData<Post> delete(Long postId) {
        if (!postRepository.existsById(postId)) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        postRepository.deleteById(postId);
        return RsData.of("S-1", "게시글이 삭제되었습니다.");
    }

    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }
}
