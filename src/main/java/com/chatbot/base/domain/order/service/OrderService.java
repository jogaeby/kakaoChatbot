package com.chatbot.base.domain.order.service;

import com.chatbot.base.domain.order.dto.OrderDto;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;

import java.util.List;

public interface OrderService {
    OrderDto orderProduct(ProductDto productDto, UserDto userDto, AddressDto addressDto);
    List<OrderDto> getOrderList(String userKey);
}
