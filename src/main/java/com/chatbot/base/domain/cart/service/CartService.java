package com.chatbot.base.domain.cart.service;

import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.UserDto;

public interface CartService {
    UserDto addProductToCart(String userKey, ProductDto productDto);

    UserDto deleteProductToCart(String userKey, String productId);
}
