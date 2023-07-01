package com.mypill.domain.address.controller;

import com.mypill.domain.address.dto.request.AddressRequest;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.address.service.AddressService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("usr/buyer/myAddress")
public class AddressController {

    private final AddressService addressService;
    private final Rq rq;

    @GetMapping("/create")
    public String createForm(){
        return "usr/address/create";
    }

    @PostMapping("/create")
    public String create(@Valid AddressRequest addressRequest){

        RsData<Address> createRsData = addressService.create(addressRequest);

        return rq.redirectWithMsg("/usr/buyer/myAddress", createRsData);
    }
}
