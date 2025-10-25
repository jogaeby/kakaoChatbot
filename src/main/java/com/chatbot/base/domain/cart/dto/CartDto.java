package com.chatbot.base.domain.cart.dto;

import com.chatbot.base.domain.product.dto.ProductDto;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class CartDto {

    @Builder.Default
    private List<ProductDto> cartItems = new ArrayList<>();
}
