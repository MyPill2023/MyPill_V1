package com.mypill.domain.post.service;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.image.service.ImageService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.post.dto.PostResponse;
import com.mypill.domain.post.dto.PostRequest;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import com.mypill.global.aws.s3.dto.AmazonS3Dto;
import com.mypill.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberService memberService;
    private final ImageService imageService;

    @Transactional
    public List<Post> getList() {
        return postRepository.findByDeleteDateIsNullOrderByCreateDateDesc();
    }

    @Transactional
    public List<Post> getMyPosts(Member member) {
        return postRepository.findByPosterIdAndDeleteDateIsNullOrderByIdDesc(member.getId());
    }

    @Transactional
    public RsData<Post> create(PostRequest postRequest, Member member, MultipartFile multipartFile) {
        if (member == null) {
            return RsData.of("F-1", "존재하지 않는 회원입니다.");
        }
        Post post = new Post(postRequest, member.getId(), new ArrayList<>());
        if (!multipartFile.isEmpty()) {
            AmazonS3Dto amazonS3ImageDto = imageService.saveImageOnServer(multipartFile, post);
            Image image = new Image(amazonS3ImageDto, multipartFile, post);
            post.addImage(image);
        }
        postRepository.save(post);
        return RsData.of("S-1", "질문 등록이 완료되었습니다.", post);
    }

    @Transactional(readOnly = true)
    public RsData<Post> showDetail(Long postId) {
        Post post = postRepository.findByIdAndDeleteDateIsNull(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        return RsData.of("S-1", "게시글이 존재합니다.", post);
    }

    @Transactional(readOnly = true)
    public RsData<Post> beforeUpdate(Long postId, Long memberId) {
        Post post = postRepository.findByIdAndDeleteDateIsNull(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        Member poster = memberService.findById(post.getPosterId()).orElse(null);
        if (poster == null) {
            return RsData.of("F-2", "존재하지 않는 게시글입니다.");
        }
        if (!Objects.equals(poster.getId(), memberId)) {
            return RsData.of("F-3", "작성자만 수정이 가능합니다.");
        }
        return RsData.of("S-1", "게시글 수정 페이지로 이동합니다.", post);
    }

    @Transactional
    public RsData<Post> update(Long postId, PostRequest postRequest, Long memberId, MultipartFile multipartFile) {
        RsData<Post> postRsData = beforeUpdate(postId, memberId);
        if (postRsData.isFail()) {
            return postRsData;
        }
        Post post = postRsData.getData();
        if (!multipartFile.isEmpty()) {
            AmazonS3Dto amazonS3ImageDto = imageService.updateImageOnServer(multipartFile, post);
            Image image = post.getImage();
            if (image == null) {
                post.addImage(new Image(amazonS3ImageDto, multipartFile, post));
            } else {
                post.getImage().update(amazonS3ImageDto, multipartFile);
            }
        }
        post.update(postRequest);
        return RsData.of("S-1", "게시글이 수정되었습니다.", post);
    }

    @Transactional
    public RsData<Post> softDelete(Long postId, Member member) {
        Post post = postRepository.findByIdAndDeleteDateIsNull(postId).orElse(null);
        if (post == null) {
            return RsData.of("F-1", "존재하지 않는 게시글입니다.");
        }
        if (!Objects.equals(post.getPosterId(), member.getId())) {
            return RsData.of("F-2", "작성자만 삭제가 가능합니다.");
        }
        post = post.toBuilder()
                .deleteDate(LocalDateTime.now())
                .build();
        postRepository.save(post);
        return RsData.of("S-1", "게시글이 삭제되었습니다.");
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(String keyword, String searchType, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if (keyword == null || searchType == null) {
            return postRepository.findPostsWithMembers(pageable);
        }
        if (searchType.equals("title")) {
            return searchTitle(keyword, pageable);
        }
        if (searchType.equals("content")) {
            return searchContent(keyword, pageable);
        }
        return postRepository.findPostsWithMembers(pageable);
    }

    public Page<PostResponse> searchTitle(String keyword, Pageable pageable) {
        return postRepository.findPostsWithMembersAndTitleContaining(keyword, pageable);
    }

    public Page<PostResponse> searchContent(String keyword, Pageable pageable) {
        return postRepository.findPostsWithMembersAndContentContaining(keyword, pageable);
    }

    public List<Post> getDeletedPosts() {
        return postRepository.findByDeleteDateIsNotNull();
    }

    @Transactional
    public void hardDelete(Post post) {
        imageService.deleteImageFromServer(post);
        postRepository.delete(post);
    }

    @Transactional
    public void whenAfterDeleteMember(Member member) {
        List<Post> posts = postRepository.findByPosterId(member.getId());
        for (Post post : posts) {
            post = post.toBuilder()
                    .deleteDate(LocalDateTime.now())
                    .build();
            postRepository.save(post);
        }
    }
}