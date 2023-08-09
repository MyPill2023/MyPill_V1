package com.mypill.domain.image.entity;

import com.mypill.domain.post.entity.Post;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.aws.s3.dto.AmazonS3Dto;
import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder(toBuilder = true)
public class Image extends BaseEntity {

    private String filepath;
    private String filename;
    private String originalUrl;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Product product;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Post post;

    public Image(AmazonS3Dto amazonS3ImageDto, MultipartFile multipartFile, ImageOperator imageOperator) {
        this.filename = multipartFile.getOriginalFilename();
        this.filepath = amazonS3ImageDto.getCdnUrl();
        this.originalUrl = amazonS3ImageDto.getOriginUrl();
        if (imageOperator instanceof Product) {
            this.product = (Product) imageOperator;
        }
        if (imageOperator instanceof Post) {
            this.post = (Post) imageOperator;
        }
    }

    public Image(AmazonS3Dto amazonS3ImageDto, MultipartFile multipartFile, Product product) {
        this.filename = multipartFile.getOriginalFilename();
        this.filepath = amazonS3ImageDto.getCdnUrl();
        this.originalUrl = amazonS3ImageDto.getOriginUrl();
        this.product = product;
    }

    public Image(AmazonS3Dto amazonS3ImageDto, MultipartFile multipartFile, Post post) {
        this.filename = multipartFile.getOriginalFilename();
        this.filepath = amazonS3ImageDto.getCdnUrl();
        this.originalUrl = amazonS3ImageDto.getOriginUrl();
        this.post = post;
    }

    public void update(AmazonS3Dto amazonS3ImageDto, MultipartFile multipartFile) {
        this.filename = multipartFile.getOriginalFilename();
        this.filepath = amazonS3ImageDto.getCdnUrl();
        this.originalUrl = amazonS3ImageDto.getOriginUrl();
    }
}
