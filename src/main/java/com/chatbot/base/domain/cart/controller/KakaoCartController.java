package com.chatbot.base.domain.cart.controller;

import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.cart.service.CartService;
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
import com.chatbot.base.dto.kakao.response.property.common.Profile;
import com.chatbot.base.dto.kakao.response.property.common.Thumbnail;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.CommerceCard;
import com.chatbot.base.dto.kakao.response.property.components.ItemCard;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/cart")
public class KakaoCartController {
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;

    private final Profile profile = new Profile("금빛방앗간","https://cafe24.poxo.com/ec01/niacom0803/5GslpdAnCPzGTb8GqqEZ3j9W8PbV9xVKJx7NVKrE/h4NpwmrqazOb++iMiMzfrbktxXZcg8qpLZQEBtTNSaMDQ==/_/web/product/extra/big/202209/11310c6c43ef8bb2556cbd066dcd26f3.jpg");

    @PostMapping(value = "list")
    public ChatBotResponse getCartList(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isPresent()) {
                Carousel carousel = new Carousel();

                UserDto userDto = maybeUser.get();
                List<ProductDto> cartItems = userDto.getCart().getCartItems();

                if (cartItems.isEmpty()) {
                    chatBotResponse.addTextCard("장바구니가 비어있습니다.");
                    return chatBotResponse;
                }

                // ✅ cartItems → Map<상품ID, 수량>
                Map<String, Integer> quantityMap = cartItems.stream()
                        .collect(Collectors.toMap(ProductDto::getId, ProductDto::getQuantity));
                Set<String> productIds = quantityMap.keySet();

                List<ProductDto> products = productService.getProducts(productIds);

                products.forEach(productDto -> {
                    int quantity = quantityMap.getOrDefault(productDto.getId(), 1);
                    Button deleteBtn = new Button("삭제",ButtonAction.블럭이동,"68fc5caf47a9e61d1aecd121",ButtonParamKey.product,productDto);

                    CommerceCard commerceCard = new CommerceCard();
                    commerceCard.setProfile(profile);
                    commerceCard.setTitle(productDto.getName());
                    commerceCard.setThumbnails(productDto.getImageUrl(),false);
                    commerceCard.setPrice(productDto.getPrice());
                    commerceCard.setDiscountRate(productDto.getDiscountRate());
                    commerceCard.setDiscountedPrice(productDto.getDiscountedPrice());
                    commerceCard.setDescription("선택 수량: "+quantity+"개");
                    commerceCard.setButton(deleteBtn);

                    carousel.addComponent(commerceCard);
                });


                chatBotResponse.addCarousel(carousel);
                chatBotResponse.addQuickButton("주문하기",ButtonAction.블럭이동,"68fc6452465dc163a642c68c");
                return chatBotResponse;
            }

            return chatBotExceptionResponse.createAuthException();
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "add/quantity")
    public ChatBotResponse addProductQuantityToCart(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isPresent()) {
                ProductDto productDto = chatBotRequest.getProduct();

                CommerceCard commerceCard = new CommerceCard();
                commerceCard.setProfile(profile);
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
                    chatBotResponse.addQuickButton(i+"개",ButtonAction.블럭이동,"68fc521e2c0d3f5ee71df50a",ButtonParamKey.product,quantityDto);
                }


                chatBotResponse.addCommerceCard(commerceCard);

                return chatBotResponse;
            }

            return chatBotExceptionResponse.createAuthException();
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "add/product")
    public ChatBotResponse addProductToCart(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isPresent()) {
                UserDto userDto = maybeUser.get();

                ProductDto productDto = chatBotRequest.getProduct();
                cartService.addProductToCart(userDto.getUserKey(),productDto);


                chatBotResponse.addTextCard("장바구니에 추가하였습니다.");
                return chatBotResponse;
            }

            return chatBotExceptionResponse.createAuthException();
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "delete/product")
    public ChatBotResponse deleteProductToCart(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isPresent()) {
                UserDto userDto = maybeUser.get();

                ProductDto productDto = chatBotRequest.getProduct();
                UserDto userDto1 = cartService.deleteProductToCart(userDto.getUserKey(), productDto.getId());


                chatBotResponse.addTextCard("장바구니에서 삭제하였습니다.");
                return chatBotResponse;
            }

            return chatBotExceptionResponse.createAuthException();
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "order/delivery")
    public ChatBotResponse orderCartDelivery(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isPresent()) {
                UserDto userDto = maybeUser.get();
                // 2. 기본 배송지 가져오기 (Optional)
                Optional<AddressDto> defaultAddressOpt = userDto.getAddressDtos()
                        .stream()
                        .filter(AddressDto::isDefaultYn)
                        .findFirst();

                if (defaultAddressOpt.isEmpty()) {
                    return chatBotExceptionResponse.createAuthException();
                }
                AddressDto defaultAddress = defaultAddressOpt.get();

                Button defaultAddressButton = new Button("선택하기",ButtonAction.블럭이동,"68fc64681d1fc539f4ee0d6f");
                defaultAddressButton.setExtra(ButtonParamKey.address,defaultAddress);

                TextCard defaultTextCard = new TextCard();
                defaultTextCard.setDescription("[기본 배송지]\n\n"+defaultAddress.getFullAddress());
                defaultTextCard.setButtons(defaultAddressButton);

                Button addAddressButton = new Button("직접 입력하기",ButtonAction.블럭이동,"68fc649b47a9e61d1aecd1fa");

                TextCard textCard = new TextCard();
                textCard.setDescription("직접 배송지를 입력 할 수 있습니다.");
                textCard.setButtons(addAddressButton);

                Carousel carousel = new Carousel();
                carousel.addComponent(defaultTextCard);
                carousel.addComponent(textCard);

                chatBotResponse.addCarousel(carousel);
                return chatBotResponse;
            }

            return chatBotExceptionResponse.createAuthException();
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "order/sheet")
    public ChatBotResponse orderCartSheet(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isPresent()) {
                UserDto userDto = maybeUser.get();
                AddressDto addressDto = chatBotRequest.getAddressDto();
                List<ProductDto> cartItems = userDto.getCart().getCartItems();
                // ✅ cartItems → Map<상품ID, 수량>
                Map<String, Integer> quantityMap = cartItems.stream()
                        .collect(Collectors.toMap(ProductDto::getId, ProductDto::getQuantity));
                Set<String> productIds = quantityMap.keySet();

                List<ProductDto> products = productService.getProducts(productIds);

                if (products.isEmpty()) {
                    chatBotResponse.addTextCard("모든 상품이 품절입니다.");
                    return chatBotResponse;
                }

                Carousel carousel = new Carousel();

                products.forEach(productDto -> {
                    int quantity = quantityMap.getOrDefault(productDto.getId(), 1);

                    CommerceCard commerceCard = new CommerceCard();
                    commerceCard.setProfile(profile);
                    commerceCard.setTitle(productDto.getName());
                    commerceCard.setThumbnails(productDto.getImageUrl(),false);
                    commerceCard.setPrice(productDto.getPrice());
                    commerceCard.setDiscountRate(productDto.getDiscountRate());
                    commerceCard.setDiscountedPrice(productDto.getDiscountedPrice());
                    commerceCard.setDescription("선택 수량: "+quantity+"개");

                    carousel.addComponent(commerceCard);
                });



                // ✅ 총 수량, 총 결제금액 계산
                int totalQuantity = 0;
                int totalPrice = 0;

                for (ProductDto product : products) {
                    int quantity = quantityMap.getOrDefault(product.getId(), 1);
                    totalQuantity += quantity;
                    totalPrice += product.getDiscountedPrice() * quantity;
                }

                // ✅ 대표 상품명 (첫 번째 상품 기준)
                ProductDto firstProduct = products.get(0);
                int otherCount = products.size() - 1;
                String productTitle = (otherCount > 0)
                        ? firstProduct.getName() + " 외 " + otherCount + "개"
                        : firstProduct.getName();


                TextCard textCard = new TextCard();
                textCard.setDescription("아래 내용으로 주문을 진행하시겠습니까?");


                ItemCard itemCard = new ItemCard();
                itemCard.setItemListAlignment("right");
                itemCard.setProfile(Map.of(
                        "title",profile.getNickname(),
                        "imageUrl",profile.getImageUrl()
                ));
                itemCard.addItemList("상품명",productTitle);
                itemCard.addItemList("수량",totalQuantity+"개");
                itemCard.setSummary("총 결제금액",String.format("%,d",totalPrice)+"원");
                itemCard.setTitle("배송지");
                itemCard.setDescription(addressDto.getFullAddress());


//                chatBotResponse.addSimpleText("아래 내용으로 주문을 진행하시겠습니까?");
                chatBotResponse.addCarousel(carousel);
//                chatBotResponse.addItemCard(itemCard);
                chatBotResponse.addQuickButton("장바구니",ButtonAction.블럭이동,"68fc4e1c2c0d3f5ee71df4bd");
                Button orderButton = new Button("주문하기",ButtonAction.블럭이동,"68df7bdc5390541970472535");
                orderButton.setExtra(ButtonParamKey.address,addressDto);
                chatBotResponse.addQuickButton(orderButton);

                StringFormatterUtil.objectToString(chatBotResponse);
                return chatBotResponse;
            }

            return chatBotExceptionResponse.createAuthException();
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }
}
