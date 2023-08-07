package com.mypill.domain.product.service;

import com.mypill.domain.member.dto.request.JoinRequest;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
        emptyFile = new MockMultipartFile("imageFile", new byte[0]);

        testUserSeller1 = memberService.join(new JoinRequest("testUserSeller123", "김철수", "1234", "testSeller123@test.com", "판매자")).getData();
        testUserSeller2 = memberService.join(new JoinRequest("testUserSeller223", "김철수", "1234",  "testSeller223@test.com", "판매자")).getData();
        product = productService.create(new ProductRequest(testUserSeller1.getId(), "테스트 상품1", "테스트 설명1", 12000L, 100L, asList(1L, 2L), asList(1L, 2L)), emptyFile).getData();
    }

    @Test
    @DisplayName("상품 등록")
    @Order(1)
    void createSuccessTests() {
        // WHEN
        Product newProduct = productService.create(new ProductRequest(testUserSeller1.getId(), "루테인 베스트", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(1L, 2L), asList(1L, 2L)), emptyFile).getData();
        Product product = productService.findById(newProduct.getId()).orElse(null);

        // THEN
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("루테인 베스트");
        assertThat(product.getSeller().getId()).isEqualTo(testUserSeller1.getId());
        assertThat(product.getPrice()).isEqualTo(12000);
    }

    @Test
    @DisplayName("상품 목록")
    @Order(2)
    void getSuccessTests() {
        // GIVEN
        Product newProduct = productService.create(new ProductRequest(testUserSeller1.getId(), "루테인 베스트", "1일 1회 1정 저녁직후에 복용하는 것이 좋습니다", 12000L, 100L, asList(1L, 2L), asList(1L, 2L)), emptyFile).getData();

        // WHEN
        RsData<Product> getRsData = productService.get(newProduct.getId());

        // THEN
        assertThat(getRsData.getResultCode()).isEqualTo("S-1");
        assertThat(getRsData.getData().getName()).isEqualTo("루테인 베스트");
        assertThat(getRsData.getData().getSeller().getId()).isEqualTo(testUserSeller1.getId());
        assertThat(getRsData.getData().getPrice()).isEqualTo(12000);
    }

    @Test
    @DisplayName("상품 수정 - 성공")
    @Order(3)
    void updateSuccessTests() {
        // GIVEN
        ProductRequest productRequest = new ProductRequest(3L, "테스트 상품 수정", "테스트 설명 수정",
                100L, 200L, asList(3L, 4L), asList(3L, 4L));

        // WHEN
        RsData<Product> updateRsData = productService.update(testUserSeller1, product.getId(), productRequest, emptyFile);
        Product updateProduct = updateRsData.getData();

        // THEN
        assertThat(updateRsData.getResultCode()).isEqualTo("S-1");
        assertThat(updateProduct.getName()).isEqualTo("테스트 상품 수정");
        assertThat(updateProduct.getDescription()).isEqualTo("테스트 설명 수정");
    }

    @Test
    @DisplayName("상품 수정 - 권한 없음 실패")
    @Order(4)
    void updateFailTests() {
        // GIVEN
        ProductRequest productRequest = new ProductRequest(3L, "테스트 상품 수정", "테스트 설명 수정",
                100L, 200L, asList(3L, 4L), asList(3L, 4L));

        // WHEN
        RsData<Product> updateRsData = productService.update(testUserSeller2, product.getId(), productRequest, emptyFile);
        Product updateProduct = updateRsData.getData();

        // THEN
        assertThat(updateRsData.getResultCode()).isEqualTo("F-2");
        assertThat(updateProduct.getName()).isEqualTo("테스트 상품1");
        assertThat(updateProduct.getDescription()).isEqualTo("테스트 설명1");
    }

    @Test
    @DisplayName("상품 삭제 - 성공")
    @Order(5)
    void deleteSuccessTests() {
        // WHEN
        RsData<Product> deleteRsData = productService.delete(testUserSeller1, product.getId());
        Product deletedProduct = productService.findById(product.getId()).orElse(null);

        // THEN
        assertThat(deleteRsData.getResultCode()).isEqualTo("S-1");
        assertThat(deletedProduct).isNotNull();
        assertThat(deletedProduct.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("상품 삭제 - 권한 없음 실패")
    @Order(6)
    void deleteFailTests() {
        //WHEN
        RsData<Product> deleteRsData = productService.delete(testUserSeller2, product.getId());
        Product deletedProduct = productService.findById(product.getId()).orElse(null);

        //THEN
        assertThat(deleteRsData.getResultCode()).isEqualTo("F-2");
        assertThat(deletedProduct).isNotNull();
        assertThat(deletedProduct.getDeleteDate()).isNull();
    }


    @Test
    @DisplayName("주문에 의한 재고 업데이트 동시성 이슈")
    @Order(7)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testUpdateStockAndSalesByOrderSuccess() throws InterruptedException{

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // ThreadPool 구성
        CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서 작업이 완료될 때까지 대기

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                        try {
                            productService.updateStockAndSalesByOrder(product.getId(), 1L);
                        }
                        finally {
                            latch.countDown();
                        }
                    }
            );
        }
        latch.await();

        Product newProduct = productService.findById(product.getId()).orElse(null);
        assertThat(newProduct).isNotNull();
        assertThat(newProduct.getStock()).isZero();
        assertThat(newProduct.getSales()).isEqualTo(100L);
    }

}

