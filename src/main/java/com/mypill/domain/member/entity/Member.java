package com.mypill.domain.member.entity;

import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.address.entity.Address;
import jakarta.persistence.*;
import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Member extends BaseEntity {
    @NotNull
    @Column(unique = true)
    private String username;
    @NotNull
    @Column(length = 8)
    private String name;
    @NotNull
    @Column(length = 80)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @NotNull
    @Column(unique = true, length = 30)
    private String email;
    private String providerTypeCode; // 카카오로 가입한 회원인지, 네이버로 가입한 회원인지
    private boolean emailVerified;

    @Column(unique = true)
    private String businessNumber;
    @Column(unique = true)
    private String nutrientBusinessNumber;

    @ManyToMany(cascade = CascadeType.ALL)
    @Builder.Default
    @JoinTable(name = "member_nutrients",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "nutrient_id")
    )
    private List<Nutrient> surveyNutrients = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Address> addresses;

    public List<GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getAuthority()));
        if (isBuyer()) {
            authorities.add(new SimpleGrantedAuthority(Role.BUYER.getAuthority()));
        }
        if (isSeller()) {
            authorities.add(new SimpleGrantedAuthority(Role.SELLER.getAuthority()));
        }
        if (isWaiter()) {
            authorities.add(new SimpleGrantedAuthority(Role.WAITER.getAuthority()));
        }
        return authorities;
    }

    public boolean isBuyer() {
        return role.equals(Role.BUYER);
    }

    public boolean isSeller() {
        return role.equals(Role.SELLER);
    }

    public boolean isWaiter() {
        return role.equals(Role.WAITER);
    }

    public void setSurveyNutrients(List<Nutrient> surveyNutrients) {
        this.surveyNutrients = surveyNutrients;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void emailVerify(){
        this.emailVerified = true;
    }
    public void updateBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
        if (this.nutrientBusinessNumber != null) {
            this.role = Role.SELLER;
        }
    }

    public void updateNutrientBusinessNumber(String nutrientBusinessNumber) {
        this.nutrientBusinessNumber = nutrientBusinessNumber;
        if (this.businessNumber != null) {
            this.role = Role.SELLER;
        }
    }

    public static Member of(JoinRequest joinRequest, String encodedPassword) {
        return Member.builder()
                .username(joinRequest.getUsername())
                .name(joinRequest.getName())
                .password(encodedPassword)
                .email(joinRequest.getEmail())
                .role(Role.findByValue(joinRequest.getUserType()))
                .build();
    }

    public static Member of(JoinRequest joinRequest, String password, boolean emailVerified) {
        return Member.builder()
                .username(joinRequest.getUsername())
                .name(joinRequest.getName())
                .password(password)
                .email(joinRequest.getEmail())
                .role(Role.findByValue(joinRequest.getUserType()))
                .emailVerified(emailVerified)
                .build();
    }

    public static Member of(String providerTypeCode, String username, String name, String email, String password, Role role) {
        return Member.builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .name(name)
                .password(password)
                .email(email)
                .role(role)
                .build();
    }
}
