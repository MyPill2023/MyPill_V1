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
    public void saveImage(MultipartFile multipartFile, Object targetObject) {
        if (!multipartFile.isEmpty()) {
            try {
                if (targetObject instanceof Product product) {
                    AmazonS3Dto amazonS3ImageDto = amazonS3Service.imageUpload(multipartFile, "product/" + UUID.randomUUID());
                    Image image = Image.builder()
                            .filename(multipartFile.getOriginalFilename())
                            .filepath(amazonS3ImageDto.getCdnUrl())
                            .product(product)
                            .build();
                    product.addImage(image);
                    imageRepository.save(image);
                } else if (targetObject instanceof Post post) {
                    AmazonS3Dto amazonS3ImageDto = amazonS3Service.imageUpload(multipartFile, "post/" + UUID.randomUUID());
                    Image image = Image.builder()
                            .filename(multipartFile.getOriginalFilename())
                            .filepath(amazonS3ImageDto.getCdnUrl())
                            .post(post)
                            .build();
                    post.addImage(image);
                    imageRepository.save(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("이미지 업로드에 실패하였습니다", e);
            }
        }
    }

    @Async
    public void updateImage(MultipartFile multipartFile, Object targetObject) {
        if (!multipartFile.isEmpty()) {
            try {
                if (targetObject instanceof Product product) {
                    AmazonS3Dto amazonS3ImageDto = amazonS3Service.imageUpload(multipartFile, "product/" + UUID.randomUUID());
                    Image image = product.getImage().toBuilder()
                            .filename(multipartFile.getOriginalFilename())
                            .filepath(amazonS3ImageDto.getCdnUrl())
                            .product(product)
                            .build();
                    product.addImage(image);
                    imageRepository.save(image);
                }
                if (targetObject instanceof Post post) {
                    AmazonS3Dto amazonS3ImageDto = amazonS3Service.imageUpload(multipartFile, "post/" + UUID.randomUUID());
                    Image image = post.getImage().toBuilder()
                            .filename(multipartFile.getOriginalFilename())
                            .filepath(amazonS3ImageDto.getCdnUrl())
                            .post(post)
                            .build();
                    post.addImage(image);
                    imageRepository.save(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("이미지 업로드에 실패하였습니다", e);
            }
        }
    }
}
