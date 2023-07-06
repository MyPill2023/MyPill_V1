package com.mypill.domain.address.controller;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.global.AppConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
class AddressControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private AddressService addressService;
    @Autowired
    private MemberService memberService;

    private Member testUser1;
    private Member testUser2;


    @BeforeEach
    void beforeEachTest() {
        testUser1 = memberService.join("testUser1", "김철수", "1234", 1, "test1@test.com").getData();
        testUser2 = memberService.join("testUser2", "김영희", "1234", "1", "test2@test.com", true).getData();
    }

    @Test
    @DisplayName("배송지 추가 성공")
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    void testCreateSuccess() throws Exception {
        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/buyer/myAddress/create")
                        .with(csrf())
                        .param("memberId", String.valueOf(testUser1.getId()))
                        .param("name", "주소 이름")
                        .param("receiverName", "수령인 이름")
                        .param("address", "주소")
                        .param("detailAddress", "상세주소")
                        .param("postCode", "우편번호")
                        .param("phoneNumber", "연락처")
                        .param("isDefault", String.valueOf(false))
                )
                .andDo(print());
        //THEN
        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/usr/buyer/myAddress**"));

        Address address = addressService.findByMemberId(testUser1.getId()).get(0);
        assertThat(address.getName()).isEqualTo("주소 이름");
        assertThat(address.getAddress()).isEqualTo("주소");
        assertFalse(address.isDefault());
    }

    @Test
    @DisplayName("배송지 추가 실패 - 최대 등록 가능 개수 초과")
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    void testCreateFail() throws Exception {
        //GIVEN
        for(int i = 0; i< AppConfig.getMaxAddressCount(); i++){
            addressService.create(new AddressRequest(testUser1.getId(), "김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true));
        }

        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/buyer/myAddress/create")
                        .with(csrf())
                        .param("memberId", String.valueOf(testUser1.getId()))
                        .param("name", "주소 이름")
                        .param("receiverName", "수령인 이름")
                        .param("address", "주소")
                        .param("detailAddress", "상세주소")
                        .param("postCode", "우편번호")
                        .param("phoneNumber", "연락처")
                        .param("isDefault", String.valueOf(false))
                )
                .andDo(print());
        //THEN
        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().is4xxClientError());

        assertThat(addressService.findByMemberId(testUser1.getId())).hasSize(AppConfig.getMaxAddressCount());
    }

    @Test
    @DisplayName("배송지 수정 성공")
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    void testUpdateSuccess() throws Exception {
        //GIVEN
        Address address = addressService.create(new AddressRequest(testUser1.getId(), "김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true)).getData();

        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/buyer/myAddress/update/%s".formatted(address.getId()))
                        .with(csrf())
                        .param("memberId", String.valueOf(testUser1.getId()))
                        .param("name", "주소 이름")
                        .param("receiverName", "수령인 이름")
                        .param("address", "주소")
                        .param("detailAddress", "상세주소")
                        .param("postCode", "우편번호")
                        .param("phoneNumber", "연락처")
                        .param("isDefault", String.valueOf(false))
                )
                .andDo(print());
        //THEN
        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/usr/buyer/myAddress**"));

        Address newAddress = addressService.findById(address.getId()).orElse(null);
        assertThat(newAddress).isNotNull();
        assertThat(newAddress.getName()).isEqualTo("주소 이름");
        assertFalse(newAddress.isDefault());
    }

    @Test
    @DisplayName("배송지 수정 실패 - 권한 없음")
    @WithMockUser(username = "testUser2", authorities = "MEMBER")
    void testUpdateFail() throws Exception {
        //GIVEN
        Address address = addressService.create(new AddressRequest(testUser1.getId(), "김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true)).getData();

        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/buyer/myAddress/update/%s".formatted(address.getId()))
                        .with(csrf())
                        .param("memberId", String.valueOf(testUser2.getId()))
                        .param("name", "주소 이름")
                        .param("receiverName", "수령인 이름")
                        .param("address", "주소")
                        .param("detailAddress", "상세주소")
                        .param("postCode", "우편번호")
                        .param("phoneNumber", "연락처")
                        .param("isDefault", String.valueOf(false))
                )
                .andDo(print());
        //THEN
        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().is4xxClientError());

        Address newAddress = addressService.findById(address.getId()).orElse(null);
        assertThat(newAddress).isNotNull();
        assertThat(newAddress.getName()).isEqualTo("김철수의 집");
        assertTrue(newAddress.isDefault());
    }

    @Test
    @DisplayName("배송지 삭제 성공")
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    void testDeleteSuccess() throws Exception {
        //GIVEN
        Address address = addressService.create(new AddressRequest(testUser1.getId(), "김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true)).getData();

        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/buyer/myAddress/delete/%s".formatted(address.getId()))
                        .with(csrf())
                )
                .andDo(print());
        //THEN
        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/usr/buyer/myAddress**"));

        Address deletedAddress = addressService.findById(address.getId()).orElse(null);
        assertThat(deletedAddress).isNotNull();
        assertThat(deletedAddress.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("배송지 삭제 실패 - 권한 없음")
    @WithMockUser(username = "testUser2", authorities = "MEMBER")
    void testDeleteFail() throws Exception {
        //GIVEN
        Address address = addressService.create(new AddressRequest(testUser1.getId(), "김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true)).getData();

        //WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/buyer/myAddress/delete/%s".formatted(address.getId()))
                        .with(csrf())
                )
                .andDo(print());
        //THEN
        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is4xxClientError());

        Address deletedAddress = addressService.findById(address.getId()).orElse(null);
        assertThat(deletedAddress).isNotNull();
        assertThat(deletedAddress.getDeleteDate()).isNull();
    }

    @Test
    @DisplayName("주문 시 배송지 세부 정보 가져오기 성공")
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    void testGetAddressDetailsSuccess() throws Exception {
        //GIVEN
        Address address = addressService.create(new AddressRequest(testUser1.getId(), "김철수의 집", "김철수", "서울시 강남구", "도산대로1", "12121", "01012341234", true)).getData();

        //WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/buyer/myAddress/getAddressDetails")
                        .param("addressId", String.valueOf(address.getId()))
                )
                .andDo(print());
        //THEN
        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("getAddressDetails"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.address").value("서울시 강남구"));
    }
    @Test
    @DisplayName("주문 시 배송지 세부 정보 가져오기 실패 - 없는 배송지")
    @WithMockUser(username = "testUser1", authorities = "MEMBER")
    void testGetAddressDetailsFail() throws Exception {
        //GIVEN
        Long addressId = 1L;

        //WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/buyer/myAddress/getAddressDetails")
                        .param("addressId", String.valueOf(addressId))
                )
                .andDo(print());
        //THEN
        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("getAddressDetails"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("배송지를 찾을 수 없습니다."));
    }


}
