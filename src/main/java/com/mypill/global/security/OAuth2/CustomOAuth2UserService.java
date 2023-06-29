package com.mypill.global.security.OAuth2;

import java.util.Collection;
import java.util.Map;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerTypeCode = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        String oauthId;
        String email = "";
        String name = "";
        if (providerTypeCode.equals("NAVER")) {
            providerTypeCode = "N";
            Map<String, String> map = (Map<String, String>) oAuth2User.getAttributes().get("response");
            email = map.get("email");
            name = map.get("name");
            oauthId = map.get("id");
        } else if (providerTypeCode.equals("KAKAO")) {
            providerTypeCode = "K";
            oauthId = oAuth2User.getName();
            name = "이름 미등록";
        } else {
            oauthId = oAuth2User.getName();
        }

        String username = providerTypeCode + "@%s".formatted(oauthId);

        Member member = memberService.whenSocialLogin(providerTypeCode, username, name, email).getData();

        return new CustomOAuth2User(member.getUsername(), member.getPassword(), member.getName(), member.getEmail(), member.getGrantedAuthorities());
    }
}

class CustomOAuth2User extends User implements OAuth2User {

    private String name;
    private String email;

    public CustomOAuth2User(String username, String password, String name, String email, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.name = name;
        this.email = email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}