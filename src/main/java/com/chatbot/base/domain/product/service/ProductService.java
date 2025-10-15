package com.chatbot.base.domain.product.service;

import com.chatbot.base.domain.product.dto.OrderDto;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getProducts();

    OrderDto orderProduct(ProductDto productDto, UserDto userDto, AddressDto addressDto);
}
