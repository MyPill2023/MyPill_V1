package com.mypill.domain.Image.service;

import com.mypill.domain.Image.entity.Image;
import com.mypill.domain.Image.repository.ImageRepository;
import com.mypill.domain.post.entity.Post;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.aws.s3.dto.AmazonS3Dto;
import com.mypill.global.aws.s3.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AmazonS3Service amazonS3Service;
    private final ImageRepository imageRepository;

    @Async
    public void saveImage (MultipartFile multipartFile, Object targetObject) {

        if (!multipartFile.isEmpty()) {

            try {
                // 이미지 업로드 및 URL 정보 받아오기
                AmazonS3Dto amazonS3ImageDto = amazonS3Service.imageUpload(multipartFile, UUID.randomUUID().toString());

                // 이미지 정보를 설정하고 저장
                Image image = Image.builder()
                        .filename(multipartFile.getOriginalFilename())
                        .filepath(amazonS3ImageDto.getCdnUrl()) // CDN URL로 변경
                        .build();

                image = imageRepository.save(image);  // 이미지를 DB에 저장

                if (targetObject instanceof Product) {
                    Product product = (Product) targetObject;
                    product.addImage(image);
                } else if (targetObject instanceof Post) {
                    Post post = (Post) targetObject;
                    post.addImage(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("이미지 업로드에 실패하였습니다", e);
            }
        }
    }
}
