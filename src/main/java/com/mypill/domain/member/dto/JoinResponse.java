package com.mypill.domain.member.dto;

import com.mypill.domain.member.entity.Member;
import com.mypill.global.rsdata.RsData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
public class JoinResponse {
    private Member member;
    private CompletableFuture<RsData<Long>> sendRsFuture;
}
