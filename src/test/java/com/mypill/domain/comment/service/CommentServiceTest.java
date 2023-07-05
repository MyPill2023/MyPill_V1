package com.mypill.domain.comment.service;

import com.mypill.domain.comment.dto.CommentRequest;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.repository.CommentRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    private CommentRequest commentRequest;
    private Member buyer;
    private Post savedPost;

    @BeforeEach()
    void beforeEach() {

        commentRequest = new CommentRequest();
        commentRequest.setNewContent("새 댓글");

        Post post = Post.builder()
                .title("글 제목")
                .content("글 내용")
                .build();

        buyer = Member.builder()
                .id(1L)
                .username("user1")
                .name("김철수")
                .password("1234")
                .userType(1)
                .email("cs@test.com")
                .build();
        buyer = memberRepository.save(buyer);
        savedPost = postRepository.save(post);
    }


    @Test
    @Transactional
    @DisplayName("댓글 작성 테스트(구매자 회원)")
    void createTest() {
        // WHEN
        Comment comment = commentService.create(commentRequest, buyer, savedPost.getId()).getData();

        // THEN
        assertTrue(commentRepository.findById(comment.getId()).isPresent());
        assertThat(commentRepository.findById(comment.getId()).get().getPost().getId()).isEqualTo(savedPost.getId());
        assertThat(commentRepository.findById(comment.getId()).get().getWriter().getId()).isEqualTo(buyer.getId());
        assertThat(commentRepository.findById(comment.getId()).get().getContent()).isEqualTo("새 댓글");
    }

    @Test
    @DisplayName("댓글 수정 테스트(구매자 회원)")
    void updateTest() {
        // GIVEN
        Comment comment = commentService.create(commentRequest, buyer, savedPost.getId()).getData();


        // WHEN
        commentRequest.setNewContent("댓글 업데이트");
        commentService.update(commentRequest, buyer, comment.getId());

        // THEN
        assertTrue(commentRepository.findById(comment.getId()).isPresent());
        assertThat(commentRepository.findById(comment.getId()).get().getPost().getId()).isEqualTo(savedPost.getId());
        assertThat(commentRepository.findById(comment.getId()).get().getWriter().getId()).isEqualTo(buyer.getId());
        assertThat(commentRepository.findById(comment.getId()).get().getContent()).isEqualTo("댓글 업데이트");
    }

    @Test
    @DisplayName("댓글 삭제 테스트(구매자 회원)")
    void deleteTest() {
        // GIVEN
        Comment comment = commentService.create(commentRequest, buyer, savedPost.getId()).getData();

        // WHEN
        commentService.delete(buyer, comment.getId());

        // THEN
        assertTrue(commentRepository.findById(comment.getId()).isPresent());
        assertThat(commentRepository.findById(comment.getId()).get().getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("내 댓글 목록 보기")
    void getMyCommentsTest() {
        // GIVEN
        commentRequest.setNewContent("새 댓글");
        commentService.create(commentRequest, buyer, savedPost.getId());
        commentService.create(commentRequest, buyer, savedPost.getId());

        Member buyer2 = Member.builder()
                .id(2L)
                .username("user2")
                .name("김영희")
                .password("1234")
                .userType(1)
                .email("yh@test.com")
                .build();
        buyer2 = memberRepository.save(buyer2);
        commentService.create(commentRequest, buyer2, savedPost.getId());

        // WHEN
        List<Comment> comments1 = commentService.getMyComments(buyer);
        List<Comment> comments2 = commentService.getMyComments(buyer2);

        // THEN
        assertThat(comments1.size()).isEqualTo(2);
        assertThat(comments2.size()).isEqualTo(1);
    }
}