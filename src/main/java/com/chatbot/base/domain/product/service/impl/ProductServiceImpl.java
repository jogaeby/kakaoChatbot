package com.chatbot.base.domain.product.service.impl;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.domain.product.dto.OrderDto;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.product.service.ProductService;
import com.chatbot.base.domain.user.dto.AddressDto;
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
    private final AlarmTalkService alarmTalkService;
    private final String ADMIN_PHONE = "01081125021";
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
    public OrderDto orderProduct(ProductDto productDto, UserDto userDto, AddressDto addressDto) {
        try {
            long id = System.currentTimeMillis();
            OrderDto order = OrderDto.builder()
                    .id(String.valueOf(id))
                    .address(addressDto)
                    .product(List.of(productDto))
                    .user(userDto)
                    .totalQuantity(productDto.getQuantity())
                    .totalPrice(productDto.getQuantity() * productDto.getDiscountedPrice())
                    .build();


            List<Object> row = new ArrayList<>();

            row.add(id);
            row.add(userDto.getUserKey());
            row.add(userDto.getName());
            row.add("'"+userDto.getPhone());
            row.add(addressDto.getFullAddress());
            row.add(productDto.getId());
            row.add(productDto.getName());
            row.add(String.valueOf(productDto.getPrice()));
            row.add(String.valueOf(productDto.getDiscountRate()));
            row.add(String.valueOf(productDto.getDiscountedPrice()));
            row.add(productDto.getQuantity());
            row.add(productDto.getQuantity()*productDto.getDiscountedPrice());
            row.add(productDto.getDescription() != null ? productDto.getDescription() : "");
            row.add(LocalDateTime.now().toString());
            row.add("접수");

            googleSheetUtil.appendToSheet(SHEET_ID,ORDER_SHEET_NAME,row);
            alarmTalkService.sendOrderReceiptToAdmin(ADMIN_PHONE,order);

            return order;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
