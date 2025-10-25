package com.chatbot.base.domain.product.service;

import com.chatbot.base.domain.order.dto.OrderDto;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface ProductService {

    List<ProductDto> getProducts();

    List<ProductDto> getProducts(Set<String> productIds);
}
