package com.mypill.global.initData;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.nutrient.Service.NutrientService;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.Service.ProductService;
import com.mypill.domain.product.dto.request.ProductRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;

import java.util.Arrays;
import java.util.List;

@Configuration
public class NotProd {

    @Bean
    CommandLineRunner initData(MemberService memberService, PasswordEncoder passwordEncoder, ProductService productService,
                               NutrientService nutrientService, CategoryService categoryService) {
        String password = passwordEncoder.encode("1234");
        return args -> {
            Member memberUser1 = memberService.join("user1", "김철수", "1234", "1", "cs@naver.com").getData();
            Member memberUser2 = memberService.join("user2", "김영희", "1234", "1", "yh@naver.com").getData();
            Member memberUser3 = memberService.join("user3", "김짱구", "1234", "2", "zzang@naver.com").getData();
            Member memberUser4 = memberService.join("user4", "김맹구", "1234", "2", "mk@naver.com").getData();
            Member memberUser5 = memberService.join("user5", "김훈이", "1234", "1", "hoon2@naver.com").getData();

            List<Nutrient> nutrients = nutrientService.findAll();
            nutrients = nutrients.subList(0, 2);

            List<Category> categories = categoryService.findAll();
            categories = categories.subList(0, 2);

            productService.create(new ProductRequest("루테인 베스트", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, nutrients, categories));

        };
    }
}
