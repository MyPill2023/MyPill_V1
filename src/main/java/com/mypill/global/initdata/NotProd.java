package com.mypill.global.initdata;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.member.dto.request.JoinRequest;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.entity.Role;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.service.OrderService;
import com.mypill.domain.product.service.ProductService;
import com.mypill.domain.product.dto.request.ProductRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;

import static java.util.Arrays.asList;

@Configuration
@Profile({"dev"})
public class NotProd {

    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            ProductService productService,
            AddressService addressService,
            OrderService orderService
    ) {
        return args -> {
            MultipartFile emptyFile = new MultipartFile() {
                @Override
                public String getName() {
                    return null;
                }

                @Override
                public String getOriginalFilename() {
                    return null;
                }

                @Override
                public String getContentType() {
                    return null;
                }

                @Override
                public boolean isEmpty() {
                    return true;
                }

                @Override
                public long getSize() {
                    return 0;
                }

                @Override
                public byte[] getBytes() {
                    return new byte[0];
                }

                @Override
                public InputStream getInputStream() {
                    return null;
                }

                @Override
                public void transferTo(File dest) throws IllegalStateException {
                }
            };
            Member memberUser1 = memberService.join(new JoinRequest("user1", "김철수", "1234", "cs@test.com", "구매자"), true).getData();
            Member memberUser2 = memberService.join(new JoinRequest("user2", "김영희", "1234", "yh@test.com", "구매자"), true).getData();
            Member memberUser3 = memberService.join(new JoinRequest("user3", "김짱구", "1234", "zzang@test.com", "판매자"), true).getData();
            Member memberUser4 = memberService.join(new JoinRequest("user4", "김맹구", "1234", "mk@test.com", "판매자"), true).getData();
            Member memberUser5 = memberService.join(new JoinRequest("user5", "김훈이", "1234", "hoon2@test.com", "구매자"), true).getData();
            Member memberUser6 = memberService.join(new JoinRequest("user6", "김멋사", "1234", "ll@test.com", "대기자"), true).getData();

            productService.create(new ProductRequest(3L, "루테인 베스트", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(1L, 2L), asList(1L, 2L)), emptyFile);
            productService.create(new ProductRequest(3L, "프로바이오틱스 글루코사민 루테인 170mg x 60캡슐", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(3L, 2L), asList(2L, 3L)), emptyFile);
            productService.create(new ProductRequest(3L, "테스트 상품3", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(4L, 3L), asList(3L, 4L)), emptyFile);
            productService.create(new ProductRequest(4L, "테스트 상품4", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(5L, 4L), asList(4L, 5L)), emptyFile);
            productService.create(new ProductRequest(4L, "테스트 상품5", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(6L, 5L), asList(5L, 6L)), emptyFile);

            AddressRequest addressRequest1 = new AddressRequest("집", "김철수", "서울특별시 중구 세종대로 110", "서울특별시청", "04524", "02-120", true);
            AddressRequest addressRequest2 = new AddressRequest("집2", "김철수", "서울특별시 중구 세종대로 110", "서울특별시청", "04524", "02-120", false);
            Address address1 = addressService.create(addressRequest1, memberUser1).getData();
            Address address2 = addressService.create(addressRequest2, memberUser1).getData();

            Order order1 = orderService.createSingleProduct(memberUser1, 1L, 3L).getData();
            Order order2 = orderService.createSingleProduct(memberUser1, 2L, 3L).getData();
            orderService.payByTossPayments(order1, "1_0001", 1L);
            orderService.payByTossPayments(order2, "2_0002", 1L);
            orderService.updatePayment(order1, "123", "카드", 36000L, LocalDateTime.now(), "Done");
            orderService.updatePayment(order2, "123", "카드", 36000L, LocalDateTime.now(), "Done");
        };
    }
}
