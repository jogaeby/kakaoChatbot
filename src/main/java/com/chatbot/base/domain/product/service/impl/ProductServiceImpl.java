package com.chatbot.base.domain.product.service.impl;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final GoogleSheetUtil googleSheetUtil;


    @Override
    public List<ProductDto> getProducts() {
        try {
            List<List<Object>> lists = googleSheetUtil.readAllSheet("12LK-mODVa9b5b8KA_m68GUF50AojwdOK7_0cok3inFM", "상품목록");
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
}
