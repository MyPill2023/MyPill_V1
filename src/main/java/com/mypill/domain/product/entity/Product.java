package com.mypill.domain.product.entity;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.category.entity.Category;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private Long price;
    @Column(nullable = false)
    private Long stock;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member seller;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Image image;

    private Long sales;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_nutrient", // 연결테이블
            joinColumns = @JoinColumn(name = "product_id"),  // Product 기본키
            inverseJoinColumns = @JoinColumn(name = "nutrient_id")  // Nutrient 기본키
    )
    private List<Nutrient> nutrients = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_category", // 연결테이블
            joinColumns = @JoinColumn(name = "product_id"),  // Product 기본키
            inverseJoinColumns = @JoinColumn(name = "category_id")  // Category 기본키
    )
    private List<Category> categories = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_member",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> likedMembers = new HashSet<>();

    public static Product of(ProductRequest requestDto, List<Nutrient> nutrients, List<Category> categories,
                             Member seller, Set<Member> likedMembers) {
        return Product.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .stock(requestDto.getStock())
                .sales(0L)
                .seller(seller)
                .nutrients(nutrients)
                .categories(categories)
                .likedMembers(likedMembers)
                .build();
    }

    public void update(ProductRequest requestDto, List<Nutrient> nutrients, List<Category> categories) {
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.price = requestDto.getPrice();
        this.stock = requestDto.getStock();
        this.nutrients = nutrients;
        this.categories = categories;
    }

    public void updateStockAndSalesByOrder(Long quantity) {
        this.stock -= quantity;
        this.sales += quantity;
    }

    public void updateStockAndSaleByOrderCancel(Long quantity) {
        this.stock += quantity;
        this.sales -= quantity;
    }

    public void addLikedMember(Member member) {
        this.likedMembers.add(member);
    }

    public void deleteLikedMember(Member member) {
        this.likedMembers.remove(member);
    }

    public void addImage(Image image) {
        this.image = image;
    }
}
