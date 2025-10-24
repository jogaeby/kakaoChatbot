package com.chatbot.base.domain.cart.converter;

import com.chatbot.base.domain.product.dto.ProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class CartItemListConverter implements AttributeConverter<List<ProductDto>,String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ProductDto> cartItems) {
        try {
            return objectMapper.writeValueAsString(cartItems);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("장바구니 직렬화 실패", e);
        }
    }

    @Override
    public List<ProductDto> convertToEntityAttribute(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(json, new TypeReference<List<ProductDto>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("장바구니 역직렬화 실패", e);
        }
    }
}
