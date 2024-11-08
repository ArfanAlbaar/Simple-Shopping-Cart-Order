package com.arfan.shop.service.cart;

import com.arfan.shop.model.Cart;
import com.arfan.shop.model.User;

import java.math.BigDecimal;

public interface CartService {

    Cart getCart(Long id);

    void clearCart(Long id);

    BigDecimal getTotalPrice(Long id);

//    Long initializeNewCart();

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}
