package com.mypill.domain.Image.entity;

import com.mypill.domain.post.entity.Post;
import com.mypill.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filepath;
    private String filename;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
