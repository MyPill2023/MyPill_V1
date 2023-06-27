package com.mypill.domain.cart.service;

import com.mypill.domain.cart.dto.request.CartProductRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductService productService;
    private final MemberService memberService;
    private final Rq rq;


    public List<CartProduct> cartView() {

        Cart cart = findByMemberId(rq.getMember().getId()).orElse(null);

        if(cart == null){
            return new ArrayList<>();
        }

        return cart.getCartProducts();
    }

    @Transactional
    public RsData<CartProduct> addProduct(CartProductRequest request) {
        Cart cart = findByMemberId(rq.getMember().getId()).orElse(null);
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

    public Optional<Cart> findByMemberId(Long MemberId){
        return cartRepository.findByMemberId(MemberId);
    }
    public Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId){
        return cartProductRepository.findByCartIdAndProductId(cartId, productId);
    }
}

