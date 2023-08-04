package com.mypill.domain.member.entity;

import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.address.entity.Address;
import jakarta.persistence.*;
import com.mypill.domain.product.entity.Product;
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
    @NotNull
    private Integer userType;
    @NotNull
    @Column(unique = true, length = 30)
    private String email;
    @Column
    private String providerTypeCode; // 카카오로 가입한 회원인지, 네이버로 가입한 회원인지
    @Column
    private boolean emailVerified;
    @Column(unique = true)
    private String businessNumber;
    @Column(unique = true)
    private String nutrientBusinessNumber;
    @ManyToMany(mappedBy = "likedMembers")
    @Builder.Default
    private Set<Product> likedProducts = new HashSet<>();
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
        authorities.add(new SimpleGrantedAuthority("MEMBER"));
        if (isBuyer()) {
            authorities.add(new SimpleGrantedAuthority("BUYER"));
        }
        if (isSeller()) {
            authorities.add(new SimpleGrantedAuthority("SELLER"));
        }
        if (isWaiter()) {
            authorities.add(new SimpleGrantedAuthority("WAITER"));
        }
        return authorities;
    }

    public boolean isBuyer() {
        return userType.equals(1);
    }

    public boolean isSeller() {
        return userType.equals(2);
    }

    public boolean isWaiter() {
        return userType.equals(3);
    }

    public void like(Product product) {
        likedProducts.add(product);
    }

    public void unLike(Product product) {
        likedProducts.remove(product);
    }

    public void setSurveyNutrients(List<Nutrient> surveyNutrients) {
        this.surveyNutrients = surveyNutrients;
    }

    public void updateBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
        if (this.nutrientBusinessNumber != null) {
            this.userType = 2;
        }
    }

    public void updateNutrientBusinessNumber(String nutrientBusinessNumber) {
        this.nutrientBusinessNumber = nutrientBusinessNumber;
        if (this.businessNumber != null) {
            this.userType = 2;
        }
    }
}
