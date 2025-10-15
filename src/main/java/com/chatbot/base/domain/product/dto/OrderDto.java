package com.chatbot.base.domain.product.dto;

import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OrderDto {
    private String id;
    private UserDto user;
    private List<ProductDto> product;
    private AddressDto address;
    private int totalQuantity;
    private int totalPrice;
}
