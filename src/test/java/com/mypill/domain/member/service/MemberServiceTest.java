package com.mypill.domain.member.service;

import com.mypill.domain.attr.service.AttrService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.product.entity.Product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private AttrService attrService;

    @Autowired
    private MemberRepository memberRepository;
    private String username1;
    private String name1;
    private String password1;
    private Integer userType1;
    private String email1;

    @BeforeEach
    void beforeEach() {
        username1 = "testUser1";
        name1 = "김철수";
        password1 = "1234";
        userType1 = 1;
        email1 = "test1@test.com";
    }

    @Test
    @DisplayName("자체회원가입 테스트")
    void joinTest() {
        // WHEN
        Member savedMember = memberService.join(username1, name1, password1, userType1, email1).getData();

        // THEN
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getUsername()).isEqualTo(username1);
        assertThat(savedMember.getName()).isEqualTo(name1);
        assertThat(savedMember.getUserType()).isEqualTo(userType1);
        assertThat(savedMember.getEmail()).isEqualTo(email1);
    }

    @Test
    @DisplayName("소셜로그인 테스트")
    void whenSocialLoginTest() {
        // GIVEN
        String providerCode = "KAKAO";

        // WHEN
        Member savedMember = memberService.whenSocialLogin(providerCode, username1, name1, email1).getData();

        // THEN
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getUsername()).isEqualTo(username1);
        assertThat(savedMember.getName()).isEqualTo(name1);
        assertThat(savedMember.getUserType()).isEqualTo(userType1);
        assertThat(savedMember.getEmail()).isEqualTo(email1);
        assertThat(savedMember.getProviderTypeCode()).isEqualTo(providerCode);
    }

    @Test
    @DisplayName("ID로 회원검색 테스트")
    void findByIdTest() {
        // GIVEN
        Member savedMember = memberService.join(username1, name1, password1, userType1, email1).getData();

        // WHEN
        Member member = memberService.findById(savedMember.getId()).orElse(null);

        // THEN
        assertThat(member).isNotNull();
        assertThat(savedMember.getId()).isEqualTo(member.getId());
        assertThat(savedMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(savedMember.getName()).isEqualTo(member.getName());
        assertThat(savedMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(savedMember.getUserType()).isEqualTo(member.getUserType());
        assertThat(savedMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("회원 아이디로 회원검색 테스트")
    void findByUsernameTest() {
        // GIVEN
        Member savedMember = memberService.join(username1, name1, password1, userType1, email1).getData();

        // WHEN
        Member member = memberService.findByUsername(username1).orElse(null);

        // THEN
        assertThat(member).isNotNull();
        assertThat(savedMember.getId()).isEqualTo(member.getId());
        assertThat(savedMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(savedMember.getName()).isEqualTo(member.getName());
        assertThat(savedMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(savedMember.getUserType()).isEqualTo(member.getUserType());
        assertThat(savedMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("회원정보 - 이름 변경")
    void updateNameTest() {
        // GIVEN
        Member savedMember = memberService.join(username1, name1, password1, userType1, email1).getData();
        String newName = "손흥민";

        // WHEN
        Member member = memberService.updateName(savedMember, newName).getData();

        // THEN
        assertThat(member).isNotNull();
        assertThat(member.getUsername()).isEqualTo(username1);
        assertThat(member.getName()).isEqualTo(newName);
        assertThat(member.getUserType()).isEqualTo(userType1);
        assertThat(member.getEmail()).isEqualTo(email1);
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteAccountTest() {
        // GIVEN
        Member member = memberService.join(username1, name1, password1, userType1, email1).getData();

        // WHEN
        Member deletedMember = memberService.deleteAccount(member).getData();

        // THEN
        assertThat(deletedMember).isNotNull();
        assertThat(deletedMember.getUsername()).isEqualTo(username1);
        assertThat(deletedMember.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("회원가입 아이디 유효성 검증 테스트1 : 입력값 없음")
    void idValidationTest1() {
        // GIVEN
        String username = "";

        // WHEN
        int result = memberService.idValidation(username);

        // THEN
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("회원가입 아이디 유효성 검증 테스트2 : 중복")
    void idValidationTest2() {
        // GIVEN
        memberService.join(username1, name1, password1, userType1, email1);
        String username = username1;

        // WHEN
        int result = memberService.idValidation(username);

        // THEN
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("회원가입 아이디 유효성 검증 테스트3 : 통과")
    void idValidationTest3() {
        // GIVEN
        memberService.join(username1, name1, password1, userType1, email1);
        String username = "testUser2";

        // WHEN
        int result = memberService.idValidation(username);

        // THEN
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트1  : 입력값 없음")
    void emailValidationTest1() {
        // GIVEN
        String email = "";

        // WHEN
        int result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트2 : 잘못된 형식")
    void emailValidationTest2() {
        // GIVEN
        String email = "testEmail@test,com";

        // WHEN
        int result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트3 : 중복")
    void emailValidationTest3() {
        // GIVEN
        memberService.join(username1, name1, password1, userType1, email1);
        String email = email1;

        // WHEN
        int result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트4 : 통과")
    void emailValidationTest4() {
        // GIVEN
        memberService.join(username1, name1, password1, userType1, email1);
        String email = "testEmail2@test.com";

        // WHEN
        int result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("좋아요 표시 이벤트를 받고, 좋아요 목록에 추가")
    void whenAfterLikeTest() {
        // GIVEN
        Member member = memberService.join(username1, name1, password1, userType1, email1).getData();
        member = member.toBuilder()
                .likedProducts(new ArrayList<>())
                .build();
        Member savedMember = memberRepository.save(member);

        String name = "name";
        String description = "description";
        Long price = 100_000L;
        Long stock = 100L;
        Product product = Product.builder()
                .id(1L)
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .build();

        // WHEN
        memberService.whenAfterLike(savedMember, product);

        // THEN
        assertThat(savedMember.getLikedProducts().size()).isEqualTo(1);
        assertThat(savedMember.getLikedProducts().get(0).getId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("좋아요 해제 이벤트를 받고, 좋아요 목록에서 삭제")
    void whenAfterUnlikeTest() {
        // GIVEN
        Member member = memberService.join(username1, name1, password1, userType1, email1).getData();
        member = member.toBuilder()
                .likedProducts(new ArrayList<>())
                .build();
        Member savedMember = memberRepository.save(member);

        String name = "name";
        String description = "description";
        Long price = 100_000L;
        Long stock = 100L;
        Product product = Product.builder()
                .id(1L)
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .build();

        // WHEN
        memberService.whenAfterLike(savedMember, product);
        memberService.whenAfterUnlike(savedMember, product);


        // THEN
        assertThat(savedMember.getLikedProducts().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("이메일 인증")
    void verifyEmailTest() {
        // GIVEN
        Member savedMember = memberService.join(username1, name1, password1, userType1, email1).getData();
        String emailVerificationCode = "member__%d__extra__emailVerificationCode".formatted(savedMember.getId());
        String attr = attrService.get(emailVerificationCode, "");

        // WHEN
        String resultCode = memberService.verifyEmail(savedMember.getId(), attr).getResultCode();

        // THEN
        assertThat(resultCode).startsWith("S-");
        assertTrue(memberRepository.findById(savedMember.getId()).isPresent());
        assertTrue(memberRepository.findById(savedMember.getId()).get().isEmailVerified());
    }

    @Test
    void surveyDelete() {

    }
}