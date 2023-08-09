package com.mypill.domain.image.service;

import com.mypill.domain.image.entity.Image;
import com.mypill.domain.image.entity.ImageOperator;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.aws.s3.dto.AmazonS3Dto;
import com.mypill.global.aws.s3.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3Service amazonS3Service;

    @Transactional
    public void save(MultipartFile imageFile, ImageOperator imageOperator) {
        if (!imageFile.isEmpty()) {
            AmazonS3Dto amazonS3ImageDto = saveImageOnServer(imageFile, imageOperator);
            Image image = new Image(amazonS3ImageDto, imageFile, imageOperator);
            imageOperator.addImage(image);
        }
    }

    @Transactional
    public void update(MultipartFile imageFile, ImageOperator imageOperator) {
        if (!imageFile.isEmpty()) {
            AmazonS3Dto amazonS3ImageDto = updateImageOnServer(imageFile, imageOperator);
            Image image = imageOperator.getImage();
            if (image == null) {
                imageOperator.addImage(new Image(amazonS3ImageDto, imageFile, imageOperator));
            } else {
                imageOperator.getImage().update(amazonS3ImageDto, imageFile);
            }
        }
    }

    @Async
    public AmazonS3Dto saveImageOnServer(MultipartFile multipartFile, ImageOperator imageOperator) {
        try {
            String folderName = imageOperator.getFolderName();
            return amazonS3Service.imageUpload(multipartFile, folderName + "/" + UUID.randomUUID());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 업로드에 실패하였습니다", e);
        }
    }

    @Async
    public AmazonS3Dto updateImageOnServer(MultipartFile multipartFile, ImageOperator imageOperator) {
        try {
            String folderName = imageOperator.getFolderName();
            deleteImageFromServer(imageOperator);
            return amazonS3Service.imageUpload(multipartFile, folderName + "/" + UUID.randomUUID());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 수정에 실패하였습니다", e);
        }
    }

    @Async
    public void deleteImageFromServer(ImageOperator imageOperator) {
        Image image = imageOperator.getImage();
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
