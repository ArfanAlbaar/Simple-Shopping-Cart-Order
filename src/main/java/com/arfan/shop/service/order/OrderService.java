package com.arfan.shop.service.order;

import com.arfan.shop.dto.OrderDto;
import com.arfan.shop.model.Order;

import java.util.List;

public interface OrderService {

    Order placeOrder(Long userId);

    OrderDto getOrderById(Long id);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertToDto(Order order);
}
