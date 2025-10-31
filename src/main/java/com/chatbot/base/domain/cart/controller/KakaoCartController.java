package com.chatbot.base.domain.cart.controller;

import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.branch.dto.BranchDto;
import com.chatbot.base.domain.branch.service.BranchService;
import com.chatbot.base.domain.cart.service.CartService;
import com.chatbot.base.domain.order.dto.OrderDto;
import com.chatbot.base.domain.order.service.OrderService;
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
    private final OrderService orderService;
    private final BranchService branchService;

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

                StringFormatterUtil.objectToString(chatBotResponse);
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

                Button addAddressButton = new Button("직접 입력하기",ButtonAction.블럭이동,"68fc6e03a02e490d3483bb24");

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

    @PostMapping(value = "order/input/delivery")
    public ChatBotResponse inputDelivery(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String delivery = chatBotRequest.getDelivery();

            AddressDto newAddressDto = AddressDto.builder()
                    .fullAddress(delivery)
                    .build();

            TextCard textCard = new TextCard();
            textCard.setDescription(delivery+"\n\n해당 배송지로 진행하시겠습니까?");


            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"68fc6e03a02e490d3483bb24");


            Button orderSheetButton = new Button("진행하기",ButtonAction.블럭이동,"68fc64681d1fc539f4ee0d6f",ButtonParamKey.address,newAddressDto);

            chatBotResponse.addQuickButton(orderSheetButton);

            return chatBotResponse;
        }catch (Exception e) {
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
                    int totalPrice = productDto.getDiscountedPrice() * quantity;

                    ItemCard itemCard = new ItemCard();
                    itemCard.setItemListAlignment("right");
                    itemCard.setProfile(Map.of(
                            "title",profile.getNickname(),
                            "imageUrl",profile.getImageUrl()
                    ));
                    itemCard.setThumbnail(new Thumbnail(productDto.getImageUrl()));
                    itemCard.addItemList("상품명",productDto.getName());
                    itemCard.addItemList("개당 가격",String.format("%,d",productDto.getDiscountedPrice())+"원");
                    itemCard.addItemList("수량",quantity+"개");
                    itemCard.setSummary("결제금액",String.format("%,d",totalPrice)+"원");

                    carousel.addComponent(itemCard);
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
                itemCard.addItemList("총 수량",totalQuantity+"개");
                itemCard.setSummary("총 결제금액",String.format("%,d",totalPrice)+"원");
                itemCard.setTitle("배송지");
                itemCard.setDescription(addressDto.getFullAddress());


                chatBotResponse.addTextCard(textCard);
                chatBotResponse.addCarousel(carousel);
                chatBotResponse.addItemCard(itemCard);
                chatBotResponse.addQuickButton("장바구니",ButtonAction.블럭이동,"68fc4e1c2c0d3f5ee71df4bd");
                Button orderButton = new Button("주문하기",ButtonAction.블럭이동,"68fc7680edb87047afe457c8",ButtonParamKey.productIds,productIds);
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

    @PostMapping(value = "order")
    public ChatBotResponse orderCart(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            // 1️⃣ 블랙리스트 체크
            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            // 2️⃣ 사용자 체크
            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());
            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }

            UserDto userDto = maybeUser.get();
            List<String> orderProductIds = chatBotRequest.getProductIds(); // 주문 요청 상품 ID
            AddressDto addressDto = chatBotRequest.getAddressDto();

            // 3️⃣ 카트에서 주문 상품만 필터링
            List<ProductDto> cartItems = userDto.getCart().getCartItems().stream()
                    .filter(p -> orderProductIds.contains(p.getId()))
                    .toList();

            // 4️⃣ DB에서 상품 조회
            Set<String> orderedIds = cartItems.stream()
                    .map(ProductDto::getId)
                    .collect(Collectors.toSet());
            List<ProductDto> dbProducts = productService.getProducts(orderedIds);

            if (orderedIds.size() != dbProducts.size()) {
                chatBotResponse.addTextCard("주문 실패: 품절된 상품이 있습니다.");
                return chatBotResponse;
            }

            // 5️⃣ ID 및 가격 검증
            List<String> invalidProducts = cartItems.stream()
                    .filter(cartItem -> {
                        ProductDto dbItem = dbProducts.stream()
                                .filter(p -> p.getId().equals(cartItem.getId()))
                                .findFirst()
                                .orElse(null);
                        return dbItem == null || dbItem.getDiscountedPrice() != cartItem.getDiscountedPrice();
                    })
                    .map(ProductDto::getId)
                    .toList();

            if (!invalidProducts.isEmpty()) {
                chatBotResponse.addTextCard("주문 실패: 가격이 일치하지 않거나 없는 상품");
                return chatBotResponse;
            }
            BranchDto branch = branchService.getBranch(chatBotRequest.getBot().getId());
            OrderDto orderDto = orderService.orderProduct(cartItems, userDto, addressDto);
            cartService.deleteProductsToCart(userDto.getUserKey(), orderedIds);
            String orderId = orderDto.getId();

            TextCard textCard = new TextCard();
            textCard.setTitle("[주문 접수 안내]");
            StringBuilder sb = new StringBuilder();

            sb.append("\n")
                    .append("고객님의 (해당 상품) 주문이 정상적으로 접수되었습니다.\n")
                    .append("접수번호: "+orderId)
                    .append("\n\n")
                    .append("입금계좌: "+branch.getAccountNum())
                    .append("\n")
                    .append("예금주: "+branch.getAccountName())
                    .append("\n")
                    .append("은행명: "+branch.getAccountBankName())
                    .append("\n\n")
                    .append("총 결제금액: "+StringFormatterUtil.formatCurrency(String.valueOf(orderDto.getTotalPrice()))+"원")
                    .append("\n\n")
                    .append("\uD83D\uDCE6 배송 안내\n")
                    .append("입금 확인 후 1~2일 이내에 우편함으로 배송되며, 배송 완료 시 문자로 안내드립니다.\n")
                    .append("*상품은 기본적으로 우편함으로 배송되며, 수량이 많을 경우 문 앞까지 배송됩니다.\n\n")
                    .append("\uD83D\uDCB0 입금 기한 안내 및 유의사항\n")
                    .append("주문일로부터 24시간 이내 미입금 시 주문은 자동 취소됩니다.\n")
                    .append("입금 시, 입금자명과 주문자명(회원명)이 다를 경우 확인이 지연될 수 있으니 유의해 주세요.");

            textCard.setDescription(sb.toString());

            chatBotResponse.addTextCard(textCard);
            return chatBotResponse;
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("주문을 실패하였습니다.");
        }
    }

}
