package com.chatbot.base.domain.product.service.impl;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.product.service.ProductService;
import com.chatbot.base.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final GoogleSheetUtil googleSheetUtil;

    private final String SHEET_ID = "12LK-mODVa9b5b8KA_m68GUF50AojwdOK7_0cok3inFM";
    private final String PRODUCT_SHEET_NAME = "상품목록";
    private final String ORDER_SHEET_NAME = "주문내역";

    @Override
    public List<ProductDto> getProducts() {
        try {
            List<List<Object>> lists = googleSheetUtil.readAllSheet(SHEET_ID, PRODUCT_SHEET_NAME);
            List<ProductDto> productDtoList = lists.stream()
                    .skip(1)
                    .map(row -> {
                        boolean soldOut = row.get(7).toString().equals("품절") ? true : false;
                        return ProductDto.builder()
                                .id(row.get(0).toString())
                                .name(row.get(1).toString())
                                .price(Integer.parseInt(row.get(2).toString()))
                                .discountRate(Integer.parseInt(row.get(3).toString()))
                                .discount(0)
                                .discountedPrice(Integer.parseInt(row.get(4).toString()))
                                .description(row.get(5).toString())
                                .imageUrl(row.get(6).toString())
                                .isSoldOut(soldOut)
                                .build();
                    })
                    .collect(Collectors.toList());


            return productDtoList;
        }catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public String orderProduct(ProductDto productDto, UserDto userDto) {
        try {
            List<Object> order = new ArrayList<>();
            long id = System.currentTimeMillis();
            order.add(id);
            order.add(userDto.getUserKey());
            order.add(userDto.getName());
            order.add(userDto.getPhone());
            order.add(userDto.getDefaultAddress().getFullAddress());
            order.add(productDto.getId());
            order.add(productDto.getName());
            order.add(String.valueOf(productDto.getPrice()));
            order.add(String.valueOf(productDto.getDiscountRate()));
            order.add(String.valueOf(productDto.getDiscountedPrice()));
            order.add(productDto.getDescription() != null ? productDto.getDescription() : "");
            order.add(LocalDateTime.now().toString());

            googleSheetUtil.appendToSheet(SHEET_ID,ORDER_SHEET_NAME,order);

            return String.valueOf(id);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
