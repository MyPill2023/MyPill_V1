package com.mypill.domain.cart.service;

import com.mypill.domain.cart.dto.request.CartProductRequest;
import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.cart.exception.OutOfStockException;
import com.mypill.domain.cart.repository.CartProductRepository;
import com.mypill.domain.cart.repository.CartRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.product.entity.Product;
import com.mypill.domain.product.service.ProductService;
import com.mypill.global.rsdata.RsData;
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
    public Cart getOrCreateCart(Member actor) {
        Cart cart = cartRepository.findByMemberId(actor.getId()).orElse(createCart(actor));
        return cartRepository.save(cart);
    }

    private Cart createCart(Member actor) {
        return Cart.createCart(actor);
    }

    @Transactional
    public RsData<CartProduct> addCartProduct(Member actor, CartProductRequest request) {
        Product product = productService.findById(request.getProductId()).orElse(null);
        if (product == null) {
            return RsData.of("F-1", "존재하지 않는 상품입니다.");
        }
        if (stockNoAvailable(product.getStock(), request.getQuantity())) {
            return RsData.of("F-2", "선택한 수량이 재고보다 많습니다.");
        }
        Cart cart = getOrCreateCart(actor);
        try {
            CartProduct cartProduct = createOrUpdateCartProduct(cart, product, request.getQuantity());
            return RsData.of("S-1", "장바구니에 추가되었습니다.", cartProduct);
        } catch (OutOfStockException e) {
            return RsData.of("F-3", "장바구니에 추가된 수량이 재고보다 많습니다.");
        }
    }

    private CartProduct createOrUpdateCartProduct(Cart cart, Product product, Long quantity) {
        CartProduct cartProduct = findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        if (cartProduct != null) {
            return updateCartProductQuantityInCart(cartProduct, product, quantity);
        }
        return createCartProduct(cart, product, quantity);
    }

    private CartProduct createCartProduct(Cart cart, Product product, Long quantity) {
        CartProduct newCartProduct = CartProduct.of(cart, product, quantity);
        cart.addCartProduct(newCartProduct);
        return cartProductRepository.save(newCartProduct);
    }

    private CartProduct updateCartProductQuantityInCart(CartProduct cartProduct, Product product, Long quantity) {
        Long updatedQuantity = cartProduct.getQuantity() + quantity;
        if (stockNoAvailable(product.getStock(), updatedQuantity)) {
            throw new OutOfStockException("재고 부족");
        }
        cartProduct.updateQuantity(updatedQuantity);
        return cartProduct;
    }

    @Transactional
    public RsData<CartProduct> updateCartProductQuantity(Member actor, Long cartProductId, Long newQuantity) {
        CartProduct cartProduct = findCartProductById(cartProductId).orElse(null);
        if (cartProduct == null) {
            return RsData.of("F-1", "장바구니에 없는 상품입니다.");
        }
        if (stockNoAvailable(cartProduct.getProduct().getStock(), newQuantity)) {
            return RsData.of("F-2", "선택한 수량이 재고보다 많습니다.");
        }
        if (hasNoPermission(actor, cartProduct)) {
            return RsData.of("F-3", "수정 권한이 없습니다.", cartProduct);
        }
        cartProduct.updateQuantity(newQuantity);
        return RsData.of("S-1", "수량이 수정되었습니다.", cartProduct);
    }

    @Transactional
    public RsData<CartProduct> hardDeleteCartProduct(Member actor, Long cartProductId) {
        CartProduct cartProduct = findCartProductById(cartProductId).orElse(null);
        if (cartProduct == null) {
            return RsData.of("F-1", "장바구니에 없는 상품입니다.");
        }
        if (hasNoPermission(actor, cartProduct)) {
            return RsData.of("F-2", "삭제 권한이 없습니다.");
        }
        cartProductRepository.delete(cartProduct);
        return RsData.of("S-1", "장바구니에서 상품이 삭제되었습니다.");
    }

    @Transactional
    public void hardDeleteCartProductByOrderId(Long orderId) {
        cartProductRepository.deleteByOrderId(orderId);
    }

    public Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId) {
        return cartProductRepository.findByCartIdAndProductIdAndDeleteDateIsNull(cartId, productId);
    }

    public Optional<CartProduct> findCartProductById(Long cartProductId) {
        return cartProductRepository.findByIdAndDeleteDateIsNull(cartProductId);
    }

    public List<CartProduct> findCartProductByIdIn(List<Long> cartProductIds) {
        return cartProductRepository.findByIdInAndDeleteDateIsNull(cartProductIds);
    }

    private boolean hasNoPermission(Member actor, CartProduct cartProduct) {
        return !actor.getId().equals(cartProduct.getCart().getMember().getId());
    }

    private boolean stockNoAvailable(Long stock, Long quantity) {
        return stock < quantity;
    }

}