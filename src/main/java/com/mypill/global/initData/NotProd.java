package com.mypill.global.initData;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.cart.service.CartService;
import com.mypill.domain.category.service.CategoryService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.nutrient.Service.NutrientService;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.product.Service.ProductService;
import com.mypill.domain.product.dto.request.ProductRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static java.util.Arrays.asList;

@Configuration
public class NotProd {

    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            PasswordEncoder passwordEncoder,
            ProductService productService,
            NutrientService nutrientService,
            CategoryService categoryService,
            CartService cartService,
            AddressService addressService,
            OrderService orderService

    ) {
        String password = passwordEncoder.encode("1234");
        return args -> {
            Member memberUser1 = memberService.join("user1", "김철수", "1234", "1", "cs@naver.com").getData();
            Member memberUser2 = memberService.join("user2", "김영희", "1234", "1", "yh@naver.com").getData();
            Member memberUser3 = memberService.join("user3", "김짱구", "1234", "2", "zzang@naver.com").getData();
            Member memberUser4 = memberService.join("user4", "김맹구", "1234", "2", "mk@naver.com").getData();
            Member memberUser5 = memberService.join("user5", "김훈이", "1234", "1", "hoon2@naver.com").getData();
            Member memberUser6 = memberService.join("user6", "김멋사", "1234", "3", "ll@naver.com").getData();

            productService.create(new ProductRequest(3L, "루테인 베스트", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(1L, 2L), asList(1L, 2L)));
            productService.create(new ProductRequest(3L, "프로바이오틱스 글루코사민 루테인 170mg x 60캡슐", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(3L, 2L), asList(2L, 3L)));
            productService.create(new ProductRequest(3L, "테스트 상품3", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(4L, 3L), asList(3L, 4L)));
            productService.create(new ProductRequest(4L, "테스트 상품4", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(5L, 4L), asList(4L, 5L)));
            productService.create(new ProductRequest(4L, "테스트 상품5", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(6L, 5L), asList(5L, 6L)));

            AddressRequest addressRequest = new AddressRequest(1L, "집","김철수", "서울특별시 중구 세종대로 110", "서울특별시청", "04524", "02-120", false);

            Address address = addressService.create(addressRequest).getData();
            Order order1 = orderService.createFromProduct(memberUser1, 1L, 3L).getData();
            Order order2 = orderService.createFromProduct(memberUser1, 2L, 3L).getData();
            orderService.payByTossPayments(order1, LocalDateTime.now(), "1_0001", 1L);
            orderService.payByTossPayments(order2, LocalDateTime.now(), "2_0002", 1L);
        };
    }
}
