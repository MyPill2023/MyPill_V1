package com.mypill.domain.cart.service;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.repository.CartProductRepository;
import com.mypill.domain.cart.repository.CartRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.product.Service.ProductService;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductService productService;
    private final Rq rq;

    @Transactional
    public RsData<Cart> addProduct(CartProductRequest request) {
        Cart cart = findByMemberId(rq.getMember().getId());
        Product product = productService.findById(request.getProductId()).orElse(null);

        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        if(cart == null){
            cart = Cart.createCart(rq.getMember());
            cartRepository.save(cart);
        }

        CartProduct cartProduct = CartProduct.of(cart, product, request.getQuantity());
        cartProductRepository.save(cartProduct);

        return RsData.of("S-1", "장바구니에 추가되었습니다.");
    }

    public Cart findByMemberId(Long MemberId){
        return cartRepository.findByMemberId(MemberId);
    }
}

