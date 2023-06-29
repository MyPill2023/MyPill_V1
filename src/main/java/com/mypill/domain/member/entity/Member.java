package com.mypill.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mypill.domain.nutrient.entity.Nutrient;
import jakarta.persistence.*;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.base.entitiy.BaseEntity;
import com.mypill.global.util.Ut;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@SuperBuilder
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
    @NotNull
    private Integer userType;
    @NotNull
    @Column(unique = true, length = 30)
    private String email;
    @Column(columnDefinition = "TEXT")
    private String accessToken;
    @Column
    private String providerTypeCode; // 카카오로 가입한 회원인지, 네이버로 가입한 회원인지
    @ManyToMany(mappedBy = "likedMembers")
    private List<Product> likedProducts = new ArrayList<>();

    public List<GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MEMBER"));
        if (isSeller()) {
            authorities.add(new SimpleGrantedAuthority("SELLER"));
        }
        return authorities;
    }

    public boolean isSeller() {
        return userType.equals(2);
    }

    public Map<String, Object> toClaims() {
        return Map.of(
                "id", getId(),
                "username", getUsername()
        );
    }

    @JsonIgnore
    public Map<String, Object> getAccessTokenClaims() {
        return Ut.mapOf(
                "id", getId(),
                "createDate", getCreateDate(),
                "username", getUsername()
        );
    }

    public void like(Product product) {
        if (!likedProducts.contains(product)) {
            likedProducts.add(product);
        }
    }

    public void unLike(Product product) {
        if (likedProducts.contains(product)) {
            likedProducts.remove(product);
        }
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "member_nutrients",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "nutrient_id")
    )
    private List<Nutrient> surveyNutrients = new ArrayList<>();
}
