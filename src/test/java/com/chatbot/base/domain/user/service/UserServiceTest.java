package com.chatbot.base.domain.user.service;

import com.chatbot.base.domain.cart.service.CartService;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;
    @Test
    void addProductToCart() {
        String userKey = "123123";

        ProductDto productDto = ProductDto.builder()
                .id("1")
                .quantity(10)
                .build();

        cartService.addProductToCart(userKey,productDto);
    }

    @Test
    void name() {
        String userKey = "123123";
        Optional<UserDto> user = userService.isUser(userKey);

        UserDto userDto = user.get();

    }
}