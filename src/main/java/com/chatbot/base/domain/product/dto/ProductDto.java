package com.chatbot.base.domain.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDto {
    private String name;
    private int price;
    private int discount;
    private int discountRate;
    private int discountedPrice;
    private String imageUrl;
    private boolean isSoldOut;
}
