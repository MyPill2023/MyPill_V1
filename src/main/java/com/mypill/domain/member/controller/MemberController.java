package com.mypill.domain.member.controller;

import com.mypill.domain.comment.dto.response.CommentsResponse;
import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.member.validation.EmailValidationResult;
import com.mypill.domain.member.validation.UsernameValidationResult;
import com.mypill.domain.post.dto.response.PostsResponse;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.post.service.PostService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "MemberController", description = "회원")
public class MemberController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    @Operation(summary = "로그인 페이지")
    public String showLogin(HttpServletRequest request, @RequestParam(required = false) String exception) {
        if (exception != null) {
            return rq.historyBack(exception);
        }
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/member/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }
        return "usr/member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    @Operation(summary = "회원가입 페이지")
    public String showJoin() {
        return "usr/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    @Operation(summary = "회원가입")
    public String join(@Valid JoinRequest joinRequest) {
        RsData<Member> joinRsData = memberService.join(joinRequest);
        if (joinRsData.isFail()) {
            return rq.historyBack(joinRsData);
        }
        return rq.redirectWithMsg("/member/login", "인증 이메일이 발송되었습니다.");
    }

    @ResponseBody
    @GetMapping("/join/usernameCheck")
    @Operation(summary = "회원가입 - 아이디 중복 체크")
    public RsData<UsernameValidationResult> usernameCheck(String username) {
        UsernameValidationResult result =  memberService.usernameValidation(username);
        return RsData.of(result.getResultCode(), result.getMessage(), result);
    }

    @ResponseBody
    @GetMapping("/join/emailCheck")
    @Operation(summary = "회원가입 - 이메일 중복 체크")
    public RsData<EmailValidationResult> emailCheck(String email) {
        EmailValidationResult result =  memberService.emailValidation(email);
        return RsData.of(result.getResultCode(), result.getMessage(), result);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    @Operation(summary = "마이페이지")
    public String showMyPage() {
        return "usr/member/myPage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myInfo")
    @Operation(summary = "내 정보 페이지")
    public String showMyInfo() {
        return "usr/member/myInfo";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPosts")
    @Operation(summary = "내 게시글 목록 페이지")
    public String showMyPosts(Model model) {
        List<Post> posts = postService.getMyPosts(rq.getMember());
        model.addAttribute("response", PostsResponse.of(posts));
        return "usr/member/myPosts";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myComments")
    @Operation(summary = "내 댓글 목록 페이지")
    public String showMyComments(Model model) {
        List<Comment> comments = commentService.getMyComments(rq.getMember());
        model.addAttribute("commentsResponse", CommentsResponse.of(comments));
        return "usr/member/myComments";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/deleteAccount")
    @Operation(summary = "회원 탈퇴")
    public void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RsData<Member> deleteRsData = memberService.softDelete(rq.getMember());
        if (deleteRsData.isFail()) {
            rq.historyBack(deleteRsData.getMsg());
        }
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        response.sendRedirect("/");
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PostMapping("/name/update")
    @Operation(summary = "이름 변경")
    public String updateName(String newName) {
        RsData<Member> updateRsData = memberService.updateName(rq.getMember(), newName);
        return updateRsData.getResultCode();
    }
}
