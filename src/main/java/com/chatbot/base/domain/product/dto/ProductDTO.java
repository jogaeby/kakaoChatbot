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

    private String memo;

    private String no;

    private String category;

    private String location;

    private String price;

    private String currentPrice;

    private String minPrice;

    private String expectedPrice;

    private LocalDate saleDate;

    private String managerName;

    private String managerPhone;

    private String description;

    private String link;

    private List<String> images;

    private String status;

    private int statusPriority;

    private LocalDate createDate;

    private String memberId;
}
