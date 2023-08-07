package com.mypill.domain.member.service;

import com.mypill.domain.attr.service.AttrService;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.repository.MemberRepository;
import com.mypill.domain.member.validation.EmailValidationResult;
import com.mypill.domain.member.validation.UsernameValidationResult;
import com.mypill.domain.product.entity.Product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private Member testUser1;

    @BeforeEach
    void beforeEach() {
        testUser1 = Member.builder()
                .username("testUser1")
                .name("김철수")
                .password("1234")
                .role(Role.SELLER)
                .email("test1@test.com")
                .emailVerified(true)
                .build();
        memberRepository.save(testUser1);
    }

    @Test
    @DisplayName("자체회원가입 테스트")
    void joinTest() {
        // WHEN
        Member testUser2 = memberService.join(new JoinRequest("testUser2", "김영희", "1234", "test2@test.com", "구매자")).getData();

        // THEN
        assertThat(testUser2).isNotNull();
        assertThat(testUser2.getUsername()).isEqualTo("testUser2");
        assertThat(testUser2.getName()).isEqualTo("김영희");
        assertThat(testUser2.getRole()).isEqualTo(Role.BUYER);
        assertThat(testUser2.getEmail()).isEqualTo("test2@test.com");
    }

    @Test
    @DisplayName("소셜로그인 테스트")
    void whenSocialLoginTest() {
        // GIVEN
        String providerCode = "K";

        // WHEN
        Member savedMember = memberService.whenSocialLogin(providerCode, "testUser3", "김짱구", "test9@test.com").getData();

        // THEN
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getUsername()).isEqualTo("testUser3");
        assertThat(savedMember.getName()).isEqualTo("김짱구");
        assertThat(savedMember.getRole()).isEqualTo(Role.BUYER);
        assertThat(savedMember.getEmail()).isEqualTo("test9@test.com");
        assertThat(savedMember.getProviderTypeCode()).isEqualTo(providerCode);
    }

    @Test
    @DisplayName("ID로 회원검색 테스트")
    void findByIdTest() {
        // WHEN
        Member member = memberService.findById(testUser1.getId()).orElse(null);

        // THEN
        assertThat(member).isNotNull();
        assertThat(testUser1.getId()).isEqualTo(member.getId());
        assertThat(testUser1.getUsername()).isEqualTo(member.getUsername());
        assertThat(testUser1.getName()).isEqualTo(member.getName());
        assertThat(testUser1.getPassword()).isEqualTo(member.getPassword());
        assertThat(testUser1.getRole()).isEqualTo(member.getRole());
        assertThat(testUser1.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("회원 아이디로 회원검색 테스트")
    void findByUsernameTest() {
        // WHEN
        Member member = memberService.findByUsername(testUser1.getUsername()).orElse(null);

        // THEN
        assertThat(member).isNotNull();
        assertThat(testUser1.getId()).isEqualTo(member.getId());
        assertThat(testUser1.getUsername()).isEqualTo(member.getUsername());
        assertThat(testUser1.getName()).isEqualTo(member.getName());
        assertThat(testUser1.getPassword()).isEqualTo(member.getPassword());
        assertThat(testUser1.getRole()).isEqualTo(member.getRole());
        assertThat(testUser1.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("회원정보 - 이름 변경")
    void updateNameTest() {
        String newName = "손흥민";

        // WHEN
        Member member = memberService.updateName(testUser1, newName).getData();

        // THEN
        assertThat(member).isNotNull();
        assertThat(member.getUsername()).isEqualTo(testUser1.getUsername());
        assertThat(member.getName()).isEqualTo(newName);
        assertThat(member.getRole()).isEqualTo(Role.SELLER);
        assertThat(member.getEmail()).isEqualTo(testUser1.getEmail());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteAccountTest() {
        // WHEN
        Member deletedMember = memberService.softDelete(testUser1).getData();

        // THEN
        assertThat(deletedMember).isNotNull();
        assertThat(deletedMember.getUsername()).isEqualTo(testUser1.getUsername());
        assertThat(deletedMember.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("회원가입 아이디 유효성 검증 테스트1 : 입력값 없음")
    void usernameValidationTest1() {
        // GIVEN
        String username = "";

        // WHEN
        UsernameValidationResult result = memberService.usernameValidation(username);

        // THEN
        assertThat(result).isEqualTo(UsernameValidationResult.USERNAME_EMPTY);
    }

    @Test
    @DisplayName("회원가입 아이디 유효성 검증 테스트2 : 중복")
    void usernameValidationTest2() {
        // GIVEN
        String username = testUser1.getUsername();

        // WHEN
        UsernameValidationResult result = memberService.usernameValidation(username);

        // THEN
        assertThat(result).isEqualTo(UsernameValidationResult.USERNAME_DUPLICATE);
    }

    @Test
    @DisplayName("회원가입 아이디 유효성 검증 테스트3 : 통과")
    void usernameValidationTest3() {
        // GIVEN
        String username = "testUser2";

        // WHEN
        UsernameValidationResult result = memberService.usernameValidation(username);

        // THEN
        assertThat(result).isEqualTo(UsernameValidationResult.VALIDATION_OK);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트1  : 입력값 없음")
    void emailValidationTest1() {
        // GIVEN
        String email = "";

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.EMAIL_EMPTY);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트2 : 잘못된 형식")
    void emailValidationTest2() {
        // GIVEN
        String email = "testEmail@test,com";

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.EMAIL_FORMAT_ERROR);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트3 : 중복")
    void emailValidationTest3() {
        // GIVEN
        String email = testUser1.getEmail();

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.EMAIL_DUPLICATE);
    }

    @Test
    @DisplayName("회원가입 이메일 유효성 검증 테스트4 : 통과")
    void emailValidationTest4() {
        // GIVEN
        String email = "testEmail2@test.com";

        // WHEN
        EmailValidationResult result = memberService.emailValidation(email);

        // THEN
        assertThat(result).isEqualTo(EmailValidationResult.VALIDATION_OK);
    }

    @Test
    @DisplayName("좋아요 표시 이벤트를 받고, 좋아요 목록에 추가")
    void whenAfterLikeTest() {
        // GIVEN
        testUser1 = testUser1.toBuilder()
                .likedProducts(new HashSet<>())
                .build();
        Member savedMember = memberRepository.save(testUser1);

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
    }

    @Test
    @DisplayName("좋아요 해제 이벤트를 받고, 좋아요 목록에서 삭제")
    void whenAfterUnlikeTest() {
        // GIVEN
        testUser1 = testUser1.toBuilder()
                .likedProducts(new HashSet<>())
                .build();
        Member savedMember = memberRepository.save(testUser1);

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
        String emailVerificationCode = "member__%d__extra__emailVerificationCode".formatted(testUser1.getId());
        String attr = attrService.get(emailVerificationCode, "");

        // WHEN
        String resultCode = memberService.verifyEmail(testUser1.getId(), attr).getResultCode();

        // THEN
        assertThat(resultCode).startsWith("S-");
        assertTrue(memberRepository.findById(testUser1.getId()).isPresent());
        assertTrue(memberRepository.findById(testUser1.getId()).get().isEmailVerified());
    }
}