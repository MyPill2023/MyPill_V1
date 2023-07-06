package com.mypill.domain.post.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.post.dto.PostRequest;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    private PostRequest postRequest;
    private Member buyer;

    @BeforeEach
    void beforeEach() {
        postRequest = new PostRequest();
        postRequest.setTitle("게시글 제목");
        postRequest.setContent("게시글 내용");

        buyer = Member.builder()
                .id(1L)
                .username("user1")
                .name("김철수")
                .password("1234")
                .userType(1)
                .email("cs@test.com")
                .build();
        buyer = memberRepository.save(buyer);
    }

    @Test
    @DisplayName("게시글 작성 테스트(구매자 회원)")
    void createTest() {
        // WHEN
        Post post = postService.create(postRequest, buyer).getData();

        // THEN
        assertTrue(postRepository.findById(post.getId()).isPresent());
        assertThat(postRepository.findById(post.getId()).get().getPoster().getId()).isEqualTo(buyer.getId());
        assertThat(postRepository.findById(post.getId()).get().getTitle()).isEqualTo(postRequest.getTitle());
        assertThat(postRepository.findById(post.getId()).get().getContent()).isEqualTo(postRequest.getContent());
    }

    @Test
    @DisplayName("게시글 수정 테스트(구매자 회원")
    void updateTest() {
        // GIVEN
        Post post = postService.create(postRequest, buyer).getData();

        // WHEN
        postRequest.setTitle("제목 업데이트");
        postRequest.setContent("내용 업데이트");
        postService.update(post.getId(), postRequest, buyer);

        // THEN
        assertTrue(postRepository.findById(post.getId()).isPresent());
        assertThat(postRepository.findById(post.getId()).get().getPoster().getId()).isEqualTo(buyer.getId());
        assertThat(postRepository.findById(post.getId()).get().getTitle()).isEqualTo("제목 업데이트");
        assertThat(postRepository.findById(post.getId()).get().getContent()).isEqualTo("내용 업데이트");
    }

    @Test
    @DisplayName("게시글 삭제 테스트(구매자 회원)")
    void deleteTest() {
        // GIVEN
        Post post = postService.create(postRequest, buyer).getData();

        // WHEN
        postService.delete(post.getId(), buyer);

        // THEN
        assertTrue(postRepository.findById(post.getId()).isPresent());
        assertThat(postRepository.findById(post.getId()).get().getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("게시글 목록 - (구매자 1 : 2개 등록 / 구매자 2 : 1개 등록) -> 반환 게시글 수 테스트")
    void getListTest() {
        // GIVEN
        Member buyer2 = Member.builder()
                .id(2L)
                .username("user2")
                .name("김영희")
                .password("1234")
                .userType(1)
                .email("yh@test.com")
                .build();
        buyer2 = memberRepository.save(buyer2);
        postService.create(postRequest, buyer);
        postService.create(postRequest, buyer);
        postService.create(postRequest, buyer2);

        // WHEN
        List<Post> posts = postService.getList();

        // THEN
        assertThat(posts.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("내 게시글 목록 - (구매자 1 : 2개 등록 / 구매자 2 : 1개 등록) -> 회원별 반환 게시글 수 테스트")
    void getMyPostsTest() {
        // GIVEN
        Member buyer2 = Member.builder()
                .id(2L)
                .username("user2")
                .name("김영희")
                .password("1234")
                .userType(1)
                .email("yh@test.com")
                .build();
        buyer2 = memberRepository.save(buyer2);
        postService.create(postRequest, buyer);
        postService.create(postRequest, buyer);
        postService.create(postRequest, buyer2);

        // WHEN
        List<Post> posts1 = postService.getMyPosts(buyer);
        List<Post> posts2 = postService.getMyPosts(buyer2);

        // THEN
        assertThat(posts1.size()).isEqualTo(2);
        assertThat(posts2.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 번호로 게시글 조회 테스트")
    void findByIdTest() {
        // GIVEN
        Post post = postService.create(postRequest, buyer).getData();

        // WHEN
        Post postFound = postService.findById(post.getId()).orElse(null);

        // THEN
        assertThat(postFound).isNotNull();
        assertThat(postFound.getPoster().getId()).isEqualTo(post.getPoster().getId());
        assertThat(postFound.getTitle()).isEqualTo(post.getTitle());
        assertThat(postFound.getContent()).isEqualTo(post.getContent());
    }

    @Test
    @DisplayName("게시글 제목 검색 테스트")
    void searchTitle() {
        // GIVEN
        String keyword = "키워드";
        postRequest.setTitle(keyword);
        postService.create(postRequest, buyer);
        postRequest.setTitle("무시");
        postService.create(postRequest, buyer);

        // WHEN
        Page<Post> postPage = postService.searchTitle(keyword, Pageable.unpaged());

        // THEN
        assertThat(postPage.getTotalElements()).isEqualTo(1);
        assertTrue(postPage.getContent().get(0).getTitle().contains(keyword));
    }

    @Test
    @DisplayName("게시글 내용 검색 테스트")
    void searchContent() {
        // GIVEN
        String keyword = "키워드";
        postRequest.setContent(keyword);
        postService.create(postRequest, buyer);
        postRequest.setContent("무시");
        postService.create(postRequest, buyer);

        // WHEN
        Page<Post> postPage = postService.searchContent(keyword, Pageable.unpaged());

        // THEN
        assertThat(postPage.getTotalElements()).isEqualTo(1);
        assertTrue(postPage.getContent().get(0).getContent().contains(keyword));
    }
}