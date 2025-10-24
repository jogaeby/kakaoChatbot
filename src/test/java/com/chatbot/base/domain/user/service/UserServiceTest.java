package com.chatbot.base.domain.user.service;

import com.chatbot.base.domain.product.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Test
    void saveProductToCart() {
        String userKey = "QFO-xDIZbO77";

        ProductDto productDto = ProductDto.builder()
                .id("1")
                .quantity(10)
                .build();

        userService.saveProductToCart(userKey,productDto);
    }

    @Test
    void name() {
        userService.join("테스트","asd1123","123123","테스트","01055554444","asdasd",true,true);
    }
}