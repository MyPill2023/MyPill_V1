package com.mypill.domain.product.service;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.rsdata.RsData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class ProductServiceTests {

    @Autowired
    private ProductService productService;
    @Autowired
    private MemberService memberService;

    private Member testUserSeller1;
    private Member testUserSeller2;
    private Product product;
    private MockMultipartFile emptyFile;

    @BeforeEach
    void beforeEachTest() {
        emptyFile = new MockMultipartFile(
                "imageFile",
                new byte[0]
        );
        testUserSeller1 = memberService.join("testUserSeller1", "김철수", "1234", 2, "testSeller1@test.com").getData();
        testUserSeller2 = memberService.join("testUserSeller2", "김철수", "1234", 2, "testSeller2@test.com").getData();
        product = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품1", "테스트 설명1", 12000L, 100L, asList(1L, 2L), asList(1L, 2L)),emptyFile).getData();
    }

    @Test
    @DisplayName("상품 등록")
    void createSuccessTests() {
        Product newProduct = productService.create(new ProductRequest(testUserSeller1.getId(), "루테인 베스트", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(1L, 2L), asList(1L, 2L)),emptyFile).getData();
        Product product = productService.findById(newProduct.getId()).orElse(null);
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("루테인 베스트");
        assertThat(product.getSeller().getId()).isEqualTo(testUserSeller1.getId());
        assertThat(product.getPrice()).isEqualTo(12000);
    }

    @Test
    @DisplayName("상품 목록")
    void getSuccessTests() {
        Product newProduct = productService.create(new ProductRequest(testUserSeller1.getId(), "루테인 베스트", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(1L, 2L), asList(1L, 2L)),emptyFile).getData();
        RsData<Product> getRsData = productService.get(newProduct.getId());
        assertThat(getRsData.getResultCode()).isEqualTo("S-1");

        assertThat(getRsData.getData().getName()).isEqualTo("루테인 베스트");
        assertThat(getRsData.getData().getSeller().getId()).isEqualTo(testUserSeller1.getId());
        assertThat(getRsData.getData().getPrice()).isEqualTo(12000);
    }

    @Test
    @DisplayName("상품 수정 - 성공")
    void updateSuccessTests() {
        RsData<Product> updateRsData = productService.update(testUserSeller1, product.getId(),
                new ProductRequest(3L, "테스트 상품 수정", "테스트 설명 수정", 100L, 200L, asList(3L, 4L), asList(3L, 4L))
                , emptyFile);
        assertThat(updateRsData.getResultCode()).isEqualTo("S-1");

        Product updateProduct = updateRsData.getData();
        assertThat(updateProduct.getName()).isEqualTo("테스트 상품 수정");
        assertThat(updateProduct.getDescription()).isEqualTo("테스트 설명 수정");
    }

    @Test
    @DisplayName("상품 수정 - 권한 없음 실패")
    void updateFailTests() {
        RsData<Product> updateRsData = productService.update(testUserSeller2, product.getId(),
                new ProductRequest(3L, "테스트 상품 수정", "테스트 설명 수정", 100L, 200L, asList(3L, 4L), asList(3L, 4L))
                , emptyFile);
        assertThat(updateRsData.getResultCode()).isEqualTo("F-2");

        Product updateProduct = updateRsData.getData();
        assertThat(updateProduct.getName()).isEqualTo("테스트 상품1");
        assertThat(updateProduct.getDescription()).isEqualTo("테스트 설명1");
    }

    @Test
    @DisplayName("상품 삭제 - 성공")
    void deleteSuccessTests() {
        RsData<Product> deleteRsData = productService.delete(testUserSeller1, product.getId());
        assertThat(deleteRsData.getResultCode()).isEqualTo("S-1");

        Product deletedProduct = productService.findById(product.getId()).orElse(null);
        assertThat(deletedProduct).isNotNull();
        assertThat(deletedProduct.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("상품 삭제 - 권한 없음 실패")
    void deleteFailTests() {
        RsData<Product> deleteRsData = productService.delete(testUserSeller2, product.getId());
        assertThat(deleteRsData.getResultCode()).isEqualTo("F-2");

        Product deletedProduct = productService.findById(product.getId()).orElse(null);
        assertThat(deletedProduct).isNotNull();
        assertThat(deletedProduct.getDeleteDate()).isNull();
    }

}
