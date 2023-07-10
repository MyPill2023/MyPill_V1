package com.mypill.domain.image.service;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.image.repository.ImageRepository;
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
                Image image = Image.builder().filename(multipartFile.getOriginalFilename())
                        .filepath(amazonS3ImageDto.getCdnUrl()) // CDN URL로 변경
                        .build();
                if (targetObject instanceof Product) {
                    Product product = (Product) targetObject;
                    image = image.toBuilder().product(product).build();
                    product.addImage(image);
                } else if (targetObject instanceof Post) {
                    Post post = (Post) targetObject;
                    image = image.toBuilder().post(post).build();
                    post.addImage(image);
                }
                imageRepository.save(image);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("이미지 업로드에 실패하였습니다", e);
            }
        }
    }

    @Async
    public void updateImage (MultipartFile multipartFile, Object targetObject) {
        if (!multipartFile.isEmpty()) {
            try {
                AmazonS3Dto amazonS3ImageDto = amazonS3Service.imageUpload(multipartFile, UUID.randomUUID().toString());
                Image image = null;
                if (targetObject instanceof Product) {
                    image = ((Product) targetObject).getImage().toBuilder()
                            .filename(multipartFile.getOriginalFilename())
                            .filepath(amazonS3ImageDto.getCdnUrl()) // CDN URL로 변경
                            .build();
                }
                if (targetObject instanceof Post) {
                    image = ((Post) targetObject).getImage().toBuilder()
                            .filename(multipartFile.getOriginalFilename())
                            .filepath(amazonS3ImageDto.getCdnUrl()) // CDN URL로 변경
                            .build();
                }
                imageRepository.save(image);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("이미지 업로드에 실패하였습니다", e);
            }
        }
    }
}
