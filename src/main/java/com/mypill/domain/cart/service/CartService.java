package com.mypill.domain.cart.service;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.repository.CartProductRepository;
import com.mypill.domain.cart.repository.CartRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductService productService;

    @Transactional
    public Cart cartView(Member actor) {
        Cart cart = findByMemberId(actor.getId());
        if(cart == null){
            cart = createCart(actor);
        }
        return cart;
    }

    @Transactional
    public RsData<CartProduct> addProduct(Member actor, CartProductRequest request) {
        Cart cart = findByMemberId(actor.getId());
        Product product = productService.findById(request.getProductId()).orElse(null);

        if(product == null){
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }

        if(!checkStockAvailability(product.getStock(), request.getQuantity())){
            return RsData.of("F-2", "선택한 수량이  재고보다 많습니다.");
        }

        if(cart == null){
            cart = createCart(actor);
        }

        CartProduct cartProduct = findOrCreateCartProduct(cart, product, request.getQuantity());

        if(cartProduct == null){
            return RsData.of("F-2", "장바구니에 추가된 수량이 재고보다 많습니다.");
        }

        return RsData.of("S-1", "장바구니에 추가되었습니다.", cartProduct);
    }

    private Cart createCart(Member actor) {
        Cart cart = Cart.createCart(actor);
        return cartRepository.save(cart);
    }

    private CartProduct findOrCreateCartProduct(Cart cart, Product product, Long quantity) {
        CartProduct existingProduct = findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);

        if (existingProduct != null) {
            Long updatedQuantity = existingProduct.getQuantity() + quantity;
            if (!checkStockAvailability(product.getStock(), updatedQuantity)) return null;
            existingProduct.updateQuantity(updatedQuantity);
            return existingProduct;
        }

        CartProduct newCartProduct = CartProduct.of(cart, product, quantity);
        newCartProduct.addCart(newCartProduct);
        return cartProductRepository.save(newCartProduct);
    }

    @Transactional
    public RsData<CartProduct> updateQuantity(Member actor, Long cartProductId, Long newQuantity) {
        Cart cart = findByMemberId(actor.getId());
        CartProduct existProduct = findCartProductById(cartProductId).orElse(null);

        if(existProduct == null){
            return RsData.of("F-1", "장바구니에 없는 상품입니다.", existProduct);
        }

        Product product = productService.findById(existProduct.getId()).orElseThrow();

        if(!checkStockAvailability(product.getStock(), newQuantity)){
            return RsData.of("F-2", "선택한 수량이 재고보다 많습니다.");
        }

        if(!hasPermisson(cart, existProduct)){
            return RsData.of("F-3", "수정 권한이 없습니다.", existProduct);
        }

        existProduct.updateQuantity(newQuantity);

        return RsData.of("S-1", "수량이 수정되었습니다.", existProduct);
    }

    @Transactional
    public RsData<CartProduct> softDeleteCartProduct(Member actor, Long cartProductId) {
        Cart cart = findByMemberId(actor.getId());
        CartProduct existProduct = findCartProductById(cartProductId).orElse(null);

        if(existProduct == null){
            return RsData.of("F-1", "장바구니에 없는 상품입니다.", existProduct);
        }

        if(!hasPermisson(cart, existProduct)){
            return RsData.of("F-2", "삭제 권한이 없습니다.", existProduct);
        }

        if(existProduct.getDeleteDate() != null){
            return RsData.of("F-3", "이미 삭제된 상품입니다.", existProduct);
        }

        existProduct.softDelete();

        return RsData.of("S-1", "장바구니에서 상품이 삭제되었습니다.", existProduct);
    }

    @Transactional
    public void deleteCartProductsByOrder(Order order) {
        List<CartProduct> cartProducts = order.getCartProducts();
        cartProducts.forEach(CartProduct::softDelete);
    }

    public Cart findByMemberId(Long memberId){
        return cartRepository.findByMemberId(memberId);
    }
    public Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId){
        return cartProductRepository.findByCartIdAndProductIdAndDeleteDateIsNull(cartId, productId);
    }
    public Optional<CartProduct> findCartProductById(Long cartProductId){
        return cartProductRepository.findById(cartProductId);
    }
    public List<CartProduct> findCartProductByIdIn(List<Long> cartProductIds){
        return cartProductRepository.findByIdIn(cartProductIds);
    }

    // cartProduct가 현재 회원 cart의 product인지
    private boolean hasPermisson(Cart cart, CartProduct cartProduct) {
        return cartProduct.getCart().getId().equals(cart.getId());
    }

    private boolean checkStockAvailability(Long stock, Long quantity){
        return stock >= quantity;
    }

}

