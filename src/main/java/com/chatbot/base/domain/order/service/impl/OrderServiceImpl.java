package com.chatbot.base.domain.order.service.impl;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.domain.order.dto.OrderDto;
import com.chatbot.base.domain.order.service.OrderService;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final GoogleSheetUtil googleSheetUtil;
    private final AlarmTalkService alarmTalkService;
    private final String ADMIN_PHONE = "01099395021";
    private final String SHEET_ID = "12LK-mODVa9b5b8KA_m68GUF50AojwdOK7_0cok3inFM";
    private final String ORDER_SHEET_NAME = "주문내역";

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

    @Override
    public List<OrderDto> getOrderList(String userKey) {
        try {
            List<List<Object>> orderList = googleSheetUtil.readAllSheet(SHEET_ID, "주문내역");

            List<OrderDto> collect = orderList.stream()
                    .filter(row -> row.get(2).equals(userKey)) // 3번째 열이 userKey인 데이터만 필터링
                    .map(row -> {
                        UserDto userDto = UserDto.builder()
                                .userKey(String.valueOf(row.get(2)))
                                .name(String.valueOf(row.get(3)))
                                .phone(String.valueOf(row.get(4)))
                                .build();

                        AddressDto addressDto = AddressDto.builder()
                                .defaultYn(true)
                                .fullAddress(String.valueOf(row.get(5)))
                                .build();


                        ProductDto productDto = ProductDto.builder()
                                .id(String.valueOf(row.get(6)))
                                .name(String.valueOf(row.get(7)))
                                .price(Integer.parseInt(row.get(8).toString()))
                                .discountRate(Integer.parseInt(row.get(9).toString()))
                                .discountedPrice(Integer.parseInt(row.get(10).toString()))
                                .description(String.valueOf(row.get(13)))
                                .build();
                        List productList = new ArrayList<>();
                        productList.add(productDto);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime localDateTime = LocalDateTime.parse(String.valueOf(row.get(14)), formatter);

                        return OrderDto.builder()
                                .id(String.valueOf(row.get(1)))
                                .product(productList)
                                .user(userDto)
                                .address(addressDto)
                                .totalQuantity(Integer.parseInt(row.get(11).toString()))
                                .totalPrice(Integer.parseInt(row.get(12).toString()))
                                .orderDate(localDateTime)
                                .status(String.valueOf(row.get(15)))
                             .build();
                    })
                    .collect(Collectors.toList());

            return collect;
        }catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
