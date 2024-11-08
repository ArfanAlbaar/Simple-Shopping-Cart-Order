package com.arfan.shop.controller;

import com.arfan.shop.dto.OrderDto;
import com.arfan.shop.exception.ResourceNotFoundException;
import com.arfan.shop.model.Order;
import com.arfan.shop.response.ApiResponse;
import com.arfan.shop.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order/{userId}/create-order")
    private ResponseEntity<ApiResponse> createOrder(@PathVariable Long userId){
        try {
            Order order = orderService.placeOrder(userId);

            OrderDto orderDto = orderService.convertToDto(order);

            return ResponseEntity.ok(new ApiResponse("Success", orderDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error Occurred!", e.getMessage()));
        }
    }

    @GetMapping("/order/{id}")
    private ResponseEntity<ApiResponse> getOrder(@PathVariable Long id) {
        try {
            OrderDto orderDto = orderService.getOrderById(id);
            return ResponseEntity.ok(new ApiResponse("Success", orderDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/order/user/{userId}")
    private ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long userId) {
        try {
            List<OrderDto> orderList = orderService.getUserOrders(userId);
            return ResponseEntity.ok(new ApiResponse("Success", orderList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error Occurred!", e.getMessage()));
        }
    }
}
