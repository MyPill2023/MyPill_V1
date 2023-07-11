package com.mypill.domain.image.entity;

import com.mypill.domain.post.entity.Post;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.aws.s3.dto.AmazonS3Dto;
import com.mypill.global.base.entitiy.BaseEntity;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filepath;
    private String filename;
    private String originalUrl;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

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

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void update(AmazonS3Dto amazonS3ImageDto, MultipartFile multipartFile) {
        this.filename = multipartFile.getOriginalFilename();
        this.filepath = amazonS3ImageDto.getCdnUrl();
        this.originalUrl = amazonS3ImageDto.getOriginUrl();
    }
}
