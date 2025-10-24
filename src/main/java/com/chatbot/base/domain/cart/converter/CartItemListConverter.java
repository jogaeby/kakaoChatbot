package com.chatbot.base.domain.cart.converter;

import com.chatbot.base.domain.product.dto.ProductDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class CartItemListConverter implements AttributeConverter<List<ProductDto>,String> {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL); // ✅ null 필드는 무시

    @Override
    public String convertToDatabaseColumn(List<ProductDto> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return "[]";
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("장바구니 직렬화 실패: " + attribute, e);
        }
    }

    @Override
    public List<ProductDto> convertToEntityAttribute(String json) {
        try {
            if (json == null || json.isBlank()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(json, new TypeReference<List<ProductDto>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("장바구니 역직렬화 실패", e);
        }
    }
}
