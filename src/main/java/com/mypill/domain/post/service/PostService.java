package com.mypill.domain.post.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.post.dto.PostRequest;
import com.mypill.domain.post.dto.PostResponse;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public List<Post> getList() {
        return postRepository.findByDeleteDateIsNullOrderByCreateDateDesc();
    }

    @Transactional
    public List<Post> getList(Member member) {
        return postRepository.findByPoster(member);
    }

    @Transactional
    public RsData<Post> create(PostRequest postRequest, Member member) {
        if (member == null) {
            return RsData.of("F-1", "존재하지 않는 회원입니다.");
        }
        Post newPost = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .poster(member)
                .build();
        postRepository.save(newPost);
        return RsData.of("S-1", "질문 등록이 완료되었습니다.", newPost);
    }

    @Transactional
    public RsData<PostResponse> get(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        return RsData.of("S-1", "존재하는 게시글입니다.", PostResponse.of(post));
    }


    @Transactional
    public RsData<Post> update(Long postId, PostRequest postRequest, Member member) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        if (post.getPoster().getId() != member.getId()) {
            return RsData.of("F-2", "작성자만 수정이 가능합니다.");
        }
        post = post.toBuilder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .build();
        postRepository.save(post);
        return RsData.of("S-1", "게시글이 수정되었습니다.", post);
    }

    @Transactional
    public RsData<Post> delete(Long postId, Member member) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        if (post.getPoster().getId() != member.getId()) {
            return RsData.of("F-2", "작성자만 삭제가 가능합니다.");
        }
        post = post.toBuilder()
                .deleteDate(LocalDateTime.now())
                .build();
        postRepository.save(post);
        return RsData.of("S-1", "게시글이 삭제되었습니다.");
    }

    @Transactional
    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }
}
