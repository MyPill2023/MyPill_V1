package com.mypill.global.aws.s3.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Repository
@RequiredArgsConstructor
public class AmazonS3Repository {

    private final AmazonS3 amazonS3;

    public PutObjectResult upload(String bucketName, String objectName, MultipartFile file, String contentType) {

        try {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(contentType);

            return amazonS3.putObject(bucketName, objectName, file.getInputStream(), metadata);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteObject(String bucketName, String objectName) {
        String fullObjectName = "i/" + objectName;
        amazonS3.deleteObject(bucketName, fullObjectName);
    }

}