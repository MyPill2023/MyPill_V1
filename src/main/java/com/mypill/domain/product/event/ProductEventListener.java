package com.mypill.domain.product.event;

import com.mypill.domain.product.service.ProductService;
import com.mypill.global.event.EventAfterLike;
import com.mypill.global.event.EventAfterUnlike;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductService productService;
    @EventListener
    public void listen(EventAfterLike event) {
        productService.whenAfterLike(event.getProduct());
    }

    @EventListener
    public void listen(EventAfterUnlike event) {
        productService.whenAfterUnlike(event.getProduct());
    }
}

