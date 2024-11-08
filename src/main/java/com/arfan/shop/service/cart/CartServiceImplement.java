package com.arfan.shop.service.cart;

import com.arfan.shop.exception.ResourceNotFoundException;
import com.arfan.shop.model.Cart;
import com.arfan.shop.model.User;
import com.arfan.shop.repository.CartItemRepository;
import com.arfan.shop.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
//import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class CartServiceImplement implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
//    private final AtomicLong cartIdGenerator = new AtomicLong(0);

    @Override
    public Cart getCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not Found"));
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long id) {
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCartId(id);
        cart.getCartItems().clear();
        cartRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = getCart(id);
        return cart.getTotalAmount();
    }

//    @Override
//    @Transactional
//    public Long initializeNewCart() {
//        Cart newCart = new Cart();
////        Long newCartId = cartIdGenerator.incrementAndGet();
////        newCart.setId(newCartId);
//        return cartRepository.save(newCart).getId();
//    }

    @Override
    @Transactional
    public Cart initializeNewCart(User user) {
        return Optional.ofNullable(getCartByUserId(user.getId()))
            .orElseGet(() -> {
                Cart cart = new Cart();
                cart.setUser(user);
                return cartRepository.save(cart);
            });
    }

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

}
