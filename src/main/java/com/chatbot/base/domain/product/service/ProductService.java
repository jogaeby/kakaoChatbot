package com.chatbot.base.domain.product.service;

import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.UserDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getProducts();

    String orderProduct(ProductDto productDto, UserDto userDto);
}
