package com.mypill.global.event;

import com.mypill.domain.product.entity.Product;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterLike extends ApplicationEvent {
    private final Product product;

    public EventAfterLike(Object source, Product product) {
        super(source);
        this.product = product;
    }
}
