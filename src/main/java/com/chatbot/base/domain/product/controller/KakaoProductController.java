package com.chatbot.base.domain.product.controller;

import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.product.service.ProductService;
import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;
import com.chatbot.base.domain.user.service.UserService;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.common.ListItem;
import com.chatbot.base.dto.kakao.response.property.common.Profile;
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
                    button = new Button("구매하기", ButtonAction.블럭이동,"123123");
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
}
