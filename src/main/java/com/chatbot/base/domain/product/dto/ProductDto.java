package com.chatbot.base.domain.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private int price;
    private int discount;
    private int discountRate;
    private int discountedPrice;
    private String imageUrl;
    private int quantity;
    private boolean isSoldOut;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
