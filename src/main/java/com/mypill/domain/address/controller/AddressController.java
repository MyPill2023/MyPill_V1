package com.mypill.domain.address.controller;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.global.AppConfig;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/buyer/myAddress")
public class AddressController {

    private final AddressService addressService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/create")
    public String createForm(){

        if(!addressService.checkCanCreate(rq.getMember().getId())){
            return rq.historyBack("배송지는 최대 "+ AppConfig.getMaxAddressCount() +"개 까지 등록 가능합니다.");
        }

        return "usr/address/create";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/create")
    public String create(@Valid AddressRequest addressRequest){
        RsData<Address> createRsData = addressService.create(addressRequest);
        if(createRsData.isFail()){
            return rq.historyBack(createRsData);
        }
        return rq.redirectWithMsg("/usr/buyer/myAddress", createRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/update/{addressId}")
    public String updateForm(@PathVariable Long addressId, Model model){

        RsData<Address> rsData = addressService.get(rq.getMember(), addressId);
        if(rsData.isFail()){
            return rq.historyBack(rsData);
        }
        model.addAttribute("address", AddressResponse.of(rsData.getData()));

        return "usr/address/update";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/update/{addressId}")
    public String update(@PathVariable Long addressId, @Valid AddressRequest addressRequest){

        RsData<Address> rsData = addressService.get(rq.getMember(), addressId);
        if(rsData.isFail()){
            return rq.historyBack(rsData);
        }
        RsData<Address> updateRsData = addressService.update(rsData.getData(), addressRequest);

        return rq.redirectWithMsg("/usr/buyer/myAddress", updateRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/delete/{addressId}")
    public String delete(@PathVariable Long addressId){

        RsData<Address> rsData = addressService.get(rq.getMember(), addressId);
        if(rsData.isFail()){
            return rq.historyBack(rsData);
        }
        RsData<Address> deleteRsData = addressService.softDelete(rsData.getData());

        return rq.redirectWithMsg("/usr/buyer/myAddress", deleteRsData);
    }

    // 주문에서 배송지 선택 시 세부정보 가져오기
    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/getAddressDetails")
    @ResponseBody
    public ResponseEntity<?> getAddressDetails(@RequestParam Long addressId) {
        Address address = addressService.findById(addressId).orElse(null);
        if (address == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("배송지를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok().body(AddressResponse.of(address));
    }
}
