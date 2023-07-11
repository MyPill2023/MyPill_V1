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

    @Async
    public AmazonS3Dto saveImageOnServer(MultipartFile multipartFile, Object object) {
        try {
            if (object instanceof Product) {
                return amazonS3Service.imageUpload(multipartFile, "product/" + UUID.randomUUID());
            } else if (object instanceof Post) {
                return amazonS3Service.imageUpload(multipartFile, "post/" + UUID.randomUUID());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 업로드에 실패하였습니다", e);
        }
    }

    @Async
    public AmazonS3Dto updateImageOnServer(MultipartFile multipartFile, Object object) {
        try {
            if (object instanceof Product product) {
                amazonS3Service.deleteImage(product.getImage().getOriginalUrl());
                return amazonS3Service.imageUpload(multipartFile, "product/" + UUID.randomUUID());
            } else if (object instanceof Post post) {
                amazonS3Service.deleteImage(post.getImage().getOriginalUrl());
                return amazonS3Service.imageUpload(multipartFile, "post/" + UUID.randomUUID());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 수정에 실패하였습니다", e);
        }
    }

    @Async
    public void deleteImageFromServer(Post post) {
        Image image = post.getImage();
        if (image == null) {
            return;
        }
        try {
            amazonS3Service.deleteImage(image.getOriginalUrl());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 삭제에 실패하였습니다", e);
        }
    }
}
