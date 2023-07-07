package com.mypill.global.aws.s3.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@RequiredArgsConstructor
@Component
@ConfigurationProperties(prefix = "custom.amazon-s3")
public class AmazonS3Properties {
    private String endPoint;
    private String regionName;
    private String accessKey;
    private String secretKey;
    private String cdnEndPoint;
    private String bucketName;
}