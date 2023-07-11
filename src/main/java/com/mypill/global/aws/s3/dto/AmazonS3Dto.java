package com.mypill.global.aws.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonS3Dto {

    private String cdnUrl;
    private String originUrl;
}