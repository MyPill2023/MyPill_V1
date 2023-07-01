package com.mypill.domain.address.controller;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("usr/buyer/myAddress")
public class AddressController {

    private final AddressService addressService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/create")
    public String createForm(){
        return "usr/address/create";
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping("/create")
    public String create(@Valid AddressRequest addressRequest){

        RsData<Address> createRsData = addressService.create(addressRequest);

        return rq.redirectWithMsg("/usr/buyer/myAddress", createRsData);
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/update/{addressId}")
    public String updateForm(@PathVariable Long addressId, Model model){

        RsData<Address> rsData = addressService.get(addressId);
        if(rsData.isFail()){
            return rq.historyBack(rsData);
        }
        model.addAttribute("address", AddressResponse.of(rsData.getData()));

        return "usr/address/update";
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping("/update/{addressId}")
    public String update(@PathVariable Long addressId, @Valid AddressRequest addressRequest){

        RsData<Address> rsData = addressService.get(addressId);
        if(rsData.isFail()){
            return rq.historyBack(rsData);
        }
        RsData<Address> updateRsData = addressService.update(rsData.getData(), addressRequest);

        return rq.redirectWithMsg("/usr/buyer/myAddress", updateRsData);
    }
}
