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
import java.util.Collections;
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

            googleSheetUtil.appendToTableSheet(SHEET_ID,ORDER_SHEET_NAME,row);
            alarmTalkService.sendOrderReceiptToAdmin(ADMIN_PHONE,order);

            return order;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderDto orderProduct(List<ProductDto> productDtos, UserDto userDto, AddressDto addressDto) {
        try {
            // 총 수량과 총 결제금액 계산
            int totalQuantity = productDtos.stream().mapToInt(ProductDto::getQuantity).sum();
            int totalPrice = productDtos.stream()
                    .mapToInt(p -> p.getQuantity() * p.getDiscountedPrice())
                    .sum();

            long id = System.currentTimeMillis();
            OrderDto order = OrderDto.builder()
                    .id(String.valueOf(id))
                    .address(addressDto)
                    .product(productDtos)
                    .user(userDto)
                    .totalQuantity(totalQuantity)
                    .totalPrice(totalPrice)
                    .build();
            // ✅ 상품명, 수량, 가격을 각각 줄바꿈으로 연결
            String ids = productDtos.stream()
                    .map(ProductDto::getId)
                    .collect(Collectors.joining("\n"));

            String names = productDtos.stream()
                    .map(ProductDto::getName)
                    .collect(Collectors.joining("\n"));

            String quantities = productDtos.stream()
                    .map(p -> String.valueOf(p.getQuantity()))
                    .collect(Collectors.joining("\n"));

            String price = productDtos.stream()
                    .map(p -> String.valueOf(p.getPrice()))
                    .collect(Collectors.joining("\n"));

            String discountedRate = productDtos.stream()
                    .map(p -> String.valueOf(p.getDiscountRate()))
                    .collect(Collectors.joining("\n"));

            String discountedPrice = productDtos.stream()
                    .map(p -> String.valueOf(p.getDiscountedPrice()))
                    .collect(Collectors.joining("\n"));

            List<Object> row = new ArrayList<>();

            row.add(id);
            row.add(userDto.getUserKey());
            row.add(userDto.getName());
            row.add("'"+userDto.getPhone());
            row.add(addressDto.getFullAddress());
            row.add(ids);
            row.add(names);
            row.add(price);
            row.add(discountedRate);
            row.add(discountedPrice);
            row.add(quantities);
            row.add(totalPrice);
            row.add("");
            row.add(LocalDateTime.now().toString());
            row.add("접수");

            googleSheetUtil.appendToTableSheet(SHEET_ID,ORDER_SHEET_NAME,row);
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
            // 리스트 자체를 뒤집음 (최신 데이터가 앞쪽으로)
            Collections.reverse(orderList);
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
                        // 상품 정보 셀 (한 셀에 여러 상품이 줄바꿈으로 들어올 수 있음)
                        String[] ids = String.valueOf(row.get(6)).split("\n");
                        String[] names = String.valueOf(row.get(7)).split("\n");
                        String[] quantities = String.valueOf(row.get(11)).split("\n"); // 수량
                        String[] prices = String.valueOf(row.get(8)).split("\n");
                        String[] discountRates = String.valueOf(row.get(9)).split("\n");
                        String[] discountedPrices = String.valueOf(row.get(10)).split("\n");
                        String[] descriptions = String.valueOf(row.get(13)).split("\n");
                        int totalQuantity = 0;

                        int productCount = ids.length; // 실제 상품 개수
                        List<ProductDto> productList = new ArrayList<>();

                        for (int i = 0; i < productCount; i++) {
                            int quantity = Integer.parseInt(quantities[i]);
                            ProductDto productDto = ProductDto.builder()
                                    .id(ids[i])
                                    .name(names[i])
                                    .price(Integer.parseInt(prices[i]))
                                    .discountRate(Integer.parseInt(discountRates[i]))
                                    .discountedPrice(Integer.parseInt(discountedPrices[i]))
                                    .description(descriptions.length > i ? descriptions[i] : "") // 없는 값은 빈 문자열 처리
                                    .build();
                            productList.add(productDto);
                            totalQuantity += quantity;               // 수량 합산
                        };

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime localDateTime = LocalDateTime.parse(String.valueOf(row.get(14)), formatter);

                        return OrderDto.builder()
                                .id(String.valueOf(row.get(1)))
                                .product(productList)
                                .user(userDto)
                                .address(addressDto)
                                .totalQuantity(totalQuantity)
                                .totalPrice(Integer.parseInt(row.get(12).toString()))
                                .orderDate(localDateTime)
                                .status(String.valueOf(row.get(15)))
                             .build();
                    })
                    .limit(10) // 최신 10개만
                    .collect(Collectors.toList());

            return collect;
        }catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
