package com.chatbot.base.domain.user.service;

import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Test
    void saveProductToCart() {
        String userKey = "123123";

        ProductDto productDto = ProductDto.builder()
                .id("1")
                .quantity(10)
                .build();

        userService.saveProductToCart(userKey,productDto);
    }

    @Test
    void name() {
        String userKey = "123123";
        Optional<UserDto> user = userService.isUser(userKey);

        UserDto userDto = user.get();

    }
}