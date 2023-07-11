package com.mypill.domain.member.controller;

import com.mypill.domain.comment.entity.Comment;
import com.mypill.domain.comment.service.CommentService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.exception.AlreadyJoinException;
import com.mypill.domain.member.form.JoinForm;
import com.mypill.domain.member.service.MemberService;
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
    public String login(HttpServletRequest request, @RequestParam(value = "exception", required = false) String exception) {
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
    public String join() {
        return "usr/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    @Operation(summary = "회원가입")
    public String join(@Valid JoinForm joinForm) {
        try {
            memberService.join(joinForm.getUsername(), joinForm.getName(), joinForm.getPassword(),
                    Integer.parseInt(joinForm.getUserType()), joinForm.getEmail());
        } catch (AlreadyJoinException e) {
            return rq.historyBack(e.getMessage());
        } catch (NumberFormatException e) {
            return rq.historyBack("회원 유형이 올바르지 않습니다.");
        }
        return rq.redirectWithMsg("/member/login", "인증 이메일이 발송되었습니다.");
    }

    @ResponseBody
    @GetMapping("/join/idCheck")
    @Operation(summary = "회원가입 - 아이디 중복 체크")
    public int idCheck(String username) {
        return memberService.idValidation(username);
    }

    @ResponseBody
    @GetMapping("/join/emailCheck")
    @Operation(summary = "회원가입 - 이메일 중복 체크")
    public int emailCheck(String email) {
        return memberService.emailValidation(email);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    @Operation(summary = "마이페이지")
    public String myPage() {
        return "usr/member/myPage";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myInfo")
    @Operation(summary = "내 정보 페이지")
    public String myInfo() {
        return "usr/member/myInfo";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPosts")
    @Operation(summary = "내 게시글 목록 페이지")
    public String myPosts(Model model) {
        List<Post> posts = postService.getMyPosts(rq.getMember());
        model.addAttribute("posts", posts);
        return "usr/member/myPosts";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myComments")
    @Operation(summary = "내 댓글 목록 페이지")
    public String myComments(Model model) {
        List<Comment> comments = commentService.getMyComments(rq.getMember());
        model.addAttribute("comments", comments);
        return "usr/member/myComments";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/deleteAccount")
    @Operation(summary = "회원 탈퇴")
    public void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RsData<Member> rsData = memberService.deleteAccount(rq.getMember());
        if (rsData.isFail()) {
            rq.historyBack(rsData.getMsg());
        }
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        response.sendRedirect("/");
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PostMapping("/name/update")
    @Operation(summary = "이름 변경")
    public String nameUpdate(String newName) {
        RsData<Member> rsData = memberService.updateName(rq.getMember(), newName);
        return rsData.getResultCode();
    }
}
