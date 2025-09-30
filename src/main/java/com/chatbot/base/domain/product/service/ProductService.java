package com.chatbot.base.domain.product.service;

import com.chatbot.base.domain.product.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProducts();
}
