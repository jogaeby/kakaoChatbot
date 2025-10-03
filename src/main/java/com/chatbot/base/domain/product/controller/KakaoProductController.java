package com.chatbot.base.domain.product.controller;

import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.product.service.ProductService;
import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;
import com.chatbot.base.domain.user.service.UserService;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.common.ListItem;
import com.chatbot.base.dto.kakao.response.property.common.Profile;
import com.chatbot.base.dto.kakao.response.property.common.Thumbnail;
import com.chatbot.base.dto.kakao.response.property.components.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/product")
public class KakaoProductController {
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    private final ProductService productService;

    private final UserService service;

    @PostMapping(value = "list")
    public ChatBotResponse productList(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            Carousel carousel = new Carousel();
            List<ProductDto> products = productService.getProducts();

            if (products.isEmpty()) {

                chatBotResponse.addTextCard("현재 판매중인 모든 상품이 품절입니다.");
                return chatBotResponse;
            }


            products.forEach(productDto -> {
                StringFormatterUtil.objectToString(productDto);
                Button button;
                if (productDto.isSoldOut()) {
                    button = new Button("품절", ButtonAction.메시지,"품절된 상품입니다.");
                }else {
                    button = new Button("구매하기", ButtonAction.블럭이동,"68df74c52c0d3f5ee7182bf2", ButtonParamKey.product,productDto);
                }

                CommerceCard commerceCard = new CommerceCard();
                commerceCard.setTitle(productDto.getName());
                commerceCard.setThumbnails(productDto.getImageUrl(),false);
                commerceCard.setPrice(productDto.getPrice());
                commerceCard.setDiscountRate(productDto.getDiscountRate());
                commerceCard.setDiscountedPrice(productDto.getDiscountedPrice());
                commerceCard.setDescription(productDto.getDescription());
                commerceCard.setButton(button);

                carousel.addComponent(commerceCard);
            });

            chatBotResponse.addCarousel(carousel);
            return chatBotResponse;
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "quantity")
    public ChatBotResponse productQuantity(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            ProductDto productDto = chatBotRequest.getProduct();

            CommerceCard commerceCard = new CommerceCard();
            commerceCard.setTitle(productDto.getName());
            commerceCard.setThumbnails(productDto.getImageUrl(),false);
            commerceCard.setPrice(productDto.getPrice());
            commerceCard.setDiscountRate(productDto.getDiscountRate());
            commerceCard.setDiscountedPrice(productDto.getDiscountedPrice());
            commerceCard.setDescription(productDto.getDescription());


            for (int i = 1; i <= 10; i++) {
                ProductDto quantityDto = ProductDto.builder()
                        .id(productDto.getId())
                        .name(productDto.getName())
                        .price(productDto.getPrice())
                        .discountRate(productDto.getDiscountRate())
                        .discountedPrice(productDto.getDiscountedPrice())
                        .imageUrl(productDto.getImageUrl())
                        .description(productDto.getDescription())
                        .quantity(i)  // 버튼별 수량 설정
                        .build();
                chatBotResponse.addQuickButton(i+"개",ButtonAction.블럭이동,"68df7a1f2c0d3f5ee7182c35",ButtonParamKey.product,quantityDto);
            }


            chatBotResponse.addCommerceCard(commerceCard);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "orderSheet")
    public ChatBotResponse productOrderSheet(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> maybeUser = service.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }

            UserDto userDto = maybeUser.get();

            ProductDto product = chatBotRequest.getProduct();

            TextCard textCard = new TextCard();
            textCard.setDescription("아래 내용으로 주문을 진행하시겠습니까?");

            TextCard delivery = new TextCard();
            delivery.setTitle("배송지");
            delivery.setDescription(userDto.getDefaultAddress().getFullAddress());

            int totalPrice = product.getDiscountedPrice() * product.getQuantity();

            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");

            itemCard.setThumbnail(new Thumbnail(product.getImageUrl()));
            itemCard.addItemList("상품명",product.getName());
            itemCard.addItemList("개당 가격",String.valueOf(product.getDiscountedPrice()));
            itemCard.addItemList("수량",String.valueOf(product.getQuantity()));
            itemCard.setSummary("총 결제금액",String.format("%,d",totalPrice)+"원");

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addTextCard(delivery);
            chatBotResponse.addQuickButton("처음으로",ButtonAction.블럭이동,"68de38ae47a9e61d1ae66a4f");
            chatBotResponse.addQuickButton("주문하기",ButtonAction.블럭이동,"68df7bdc5390541970472535",ButtonParamKey.product,product);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "order")
    public ChatBotResponse productOrder(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> maybeUser = service.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }


            ProductDto product = chatBotRequest.getProduct();
            UserDto userDto = maybeUser.get();

            String orderId = productService.orderProduct(product, userDto);

            TextCard textCard = new TextCard();
            textCard.setTitle("["+orderId+"] 주문 성공");
            textCard.setDescription("주문을 성공적으로 완료하였습니다.");

            chatBotResponse.addTextCard(textCard);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("주문을 실패하였습니다.");
        }
    }
}
