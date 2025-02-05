package com.chatbot.base.domain.product.dto;

import com.chatbot.base.domain.product.constant.ProductStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@ToString
@Getter
@Builder
public class ProductDTO {
    private String id;
    private String title;
    private String description;
    private String link;
    private List<String> images;
    private String status;
    private LocalDate createDate;
    private String memberId;
}
