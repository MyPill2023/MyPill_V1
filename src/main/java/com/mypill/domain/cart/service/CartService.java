package com.mypill.domain.cart.service;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.dto.response.CartResponse;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.repository.CartProductRepository;
import com.mypill.domain.cart.repository.CartRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.product.Service.ProductService;
import com.mypill.domain.product.entity.Product;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductService productService;
    private final MemberService memberService;
    private final Rq rq;


    public CartResponse cartView() {

        Cart cart = findByMemberId(rq.getMember().getId());

        if(cart == null){
            return new CartResponse();
        }

        return CartResponse.of(cart);
    }

    @Transactional
    public RsData<CartProduct> addProduct(CartProductRequest request) {
        Cart cart = findByMemberId(rq.getMember().getId());
        Product product = productService.findById(request.getProductId()).orElse(null);

        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        if(cart == null){
            cart = Cart.createCart(rq.getMember());
            cartRepository.save(cart);
        }

        CartProduct existProduct = findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        if(existProduct != null){
            existProduct.updateQuantity(existProduct.getQuantity() + request.getQuantity());
            return RsData.of("S-1", "장바구니에 수량이 추가되었습니다.", existProduct);
        }

        CartProduct cartProduct = CartProduct.of(cart, product, request.getQuantity());
        cartProductRepository.save(cartProduct);

        return RsData.of("S-1", "장바구니에 추가되었습니다.", cartProduct);
    }

    @Transactional
    public RsData<CartProduct> updateQuantity(Long cartProductId, int newQuantity) {
        Cart cart = findByMemberId(rq.getMember().getId());
        CartProduct existProduct = findById(cartProductId).orElse(null);

        if(existProduct == null){
            return RsData.of("F-1", "장바구니에 없는 상품입니다.", existProduct);
        }

        if(!hasPermisson(cart, existProduct)){
            return RsData.of("F-2", "수정 권한이 없습니다.", existProduct);
        }

        existProduct.updateQuantity(newQuantity);

        return RsData.of("S-1", "수량이 수정되었습니다.", existProduct);
    }

    public RsData<CartProduct> softDeleteCartProduct(Long cartProductId) {
        Cart cart = findByMemberId(rq.getMember().getId());
        CartProduct existProduct = findById(cartProductId).orElse(null);

        if(existProduct == null){
            return RsData.of("F-1", "장바구니에 없는 상품입니다.", existProduct);
        }

        if(!hasPermisson(cart, existProduct)){
            return RsData.of("F-2", "삭제 권한이 없습니다.", existProduct);
        }

        if(existProduct.getDeleteDate() != null){
            return RsData.of("F-3", "이미 삭제된 상품입니다.", existProduct);
        }

        existProduct = existProduct.toBuilder().deleteDate(LocalDateTime.now()).build();
        cartProductRepository.save(existProduct);

        return RsData.of("S-1", "장바구니에서 상품이 삭제되었습니다.", existProduct);
    }

    public Cart findByMemberId(Long MemberId){
        return cartRepository.findByMemberId(MemberId);
    }
    public Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId){
        return cartProductRepository.findByCartIdAndProductIdAndDeleteDateIsNull(cartId, productId);
    }
    public Optional<CartProduct> findById(Long cartProductId){
        return cartProductRepository.findById(cartProductId);
    }

    // cartProduct가 현재 회원 cart의 product인지
    private boolean hasPermisson(Cart cart, CartProduct cartProduct) {
        return cartProduct.getCart().getId().equals(cart.getId());
    }
}

