package com.arfan.shop.controller;

import com.arfan.shop.exception.ResourceNotFoundException;
import com.arfan.shop.model.Cart;
import com.arfan.shop.model.User;
import com.arfan.shop.response.ApiResponse;
import com.arfan.shop.service.cart.CartItemService;
import com.arfan.shop.service.cart.CartService;
import com.arfan.shop.service.user.UserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/item")
public class CartItemController {

    private final CartItemService cartItemService;
    private final CartService cartService;

    private final UserService userService;

//    @PostMapping("/add")
//    private ResponseEntity<ApiResponse> addItem(
//            @RequestParam(required = false) Long cartId,
//            @RequestParam Long productId,
//            @RequestParam(name = "qty", defaultValue = "1") int quantity) {
//        try {
//
//            if (cartId == null) {
//                cartId = cartService.initializeNewCart();
//            }
//            cartItemService.addItemToCart(cartId, productId, quantity);
//            return ResponseEntity.ok(new ApiResponse("Item added to Cart", null));
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
//        }
//    }


    @PostMapping("/add")
    private ResponseEntity<ApiResponse> addItem(
            @RequestParam Long productId,
            @RequestParam(name = "qty", defaultValue = "1") int quantity) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.initializeNewCart(user);
            cartItemService.addItemToCart(cart.getId(), productId, quantity);
            return ResponseEntity.ok(new ApiResponse("Item added to Cart", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (JwtException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        }
    }



    @DeleteMapping("/remove")
    private ResponseEntity<ApiResponse> removeItemFromCart(
            @RequestParam(required = false) Long cartId,
            @RequestParam Long productId) {
        try {
            cartItemService.removeItemFromCart(cartId, productId);
            return ResponseEntity.ok(new ApiResponse("Item removed", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/update")
    private ResponseEntity<ApiResponse> updateItemQuantity(
            @RequestParam(required = false) Long cartId,
            @RequestParam Long productId,
            @RequestParam(name = "qty", defaultValue = "1") int quantity) {
        try {
            if (quantity <= 0) {
                cartItemService.removeItemFromCart(cartId, productId);
                return ResponseEntity.ok(new ApiResponse("Item removed", null));
            } else {
                cartItemService.updateItemQuantity(cartId, productId, quantity);
                return ResponseEntity.ok(new ApiResponse("Update item success!", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }


}
