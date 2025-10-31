package com.chatbot.base.domain.product.controller;

import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.branch.dto.BranchDto;
import com.chatbot.base.domain.branch.service.BranchService;
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
import com.chatbot.base.dto.kakao.response.property.components.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/product")
public class KakaoProductController {
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    private final ProductService productService;

    private final OrderService orderService;

    private final UserService userService;
    private final BranchService branchService;

    private final Profile profile = new Profile("금빛방앗간","https://cafe24.poxo.com/ec01/niacom0803/5GslpdAnCPzGTb8GqqEZ3j9W8PbV9xVKJx7NVKrE/h4NpwmrqazOb++iMiMzfrbktxXZcg8qpLZQEBtTNSaMDQ==/_/web/product/extra/big/202209/11310c6c43ef8bb2556cbd066dcd26f3.jpg");

    @PostMapping(value = "list")
    public ChatBotResponse productList(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            Carousel carousel = new Carousel();
            List<ProductDto> products = productService.getProducts();

            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            if (products.isEmpty()) {
                chatBotResponse.addTextCard("현재 판매중인 모든 상품이 품절입니다.");
                return chatBotResponse;
            }


            products.forEach(productDto -> {
                Button detailBtn = new Button("상세보기",ButtonAction.블럭이동,"68f6cedda02e490d34823ac2",ButtonParamKey.product,productDto);
                Button button;

                if (productDto.isSoldOut()) {
                    button = new Button("품절", ButtonAction.메시지,"품절된 상품입니다.");
                }else {
                    button = new Button("구매하기", ButtonAction.블럭이동,"68df74c52c0d3f5ee7182bf2", ButtonParamKey.product,productDto);
                }

                CommerceCard commerceCard = new CommerceCard();
                commerceCard.setProfile(profile);
                commerceCard.setTitle(productDto.getName());
                commerceCard.setThumbnails(productDto.getImageUrl(),false);
                commerceCard.setPrice(productDto.getPrice());
                commerceCard.setDiscountRate(productDto.getDiscountRate());
                commerceCard.setDiscountedPrice(productDto.getDiscountedPrice());
                commerceCard.setDescription(productDto.getDescription());
                commerceCard.setButton(button);
                commerceCard.setButton(detailBtn);
                if (!productDto.isSoldOut()) {
                    Button cartBtn = new Button("장바구니 담기",ButtonAction.블럭이동,"68fc5162edb87047afe45475",ButtonParamKey.product,productDto);
                    commerceCard.setButton(cartBtn);
                }

                carousel.addComponent(commerceCard);
            });

            chatBotResponse.addCarousel(carousel);
            return chatBotResponse;
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "detail")
    public ChatBotResponse productDetail(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            ProductDto product = chatBotRequest.getProduct();

            Button detailBtn = new Button("상세보기",ButtonAction.블럭이동,"68f6cedda02e490d34823ac2",ButtonParamKey.product,product);
            Button button;
            if (product.isSoldOut()) {
                button = new Button("품절", ButtonAction.메시지,"품절된 상품입니다.");
            }else {
                button = new Button("구매하기", ButtonAction.블럭이동,"68df74c52c0d3f5ee7182bf2", ButtonParamKey.product,product);
            }

            CommerceCard commerceCard = new CommerceCard();
            commerceCard.setProfile(profile);
            commerceCard.setTitle(product.getName());
            commerceCard.setThumbnails(product.getImageUrl(),false);
            commerceCard.setPrice(product.getPrice());
            commerceCard.setDiscountRate(product.getDiscountRate());
            commerceCard.setDiscountedPrice(product.getDiscountedPrice());
            commerceCard.setDescription(product.getDescription());
            commerceCard.setButton(button);
            commerceCard.setButton(detailBtn);
            if (!product.isSoldOut()) {
                Button cartBtn = new Button("장바구니 담기",ButtonAction.블럭이동,"68fc5162edb87047afe45475",ButtonParamKey.product,product);
                commerceCard.setButton(cartBtn);
            }
            chatBotResponse.addCommerceCard(commerceCard);

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

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }

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
                chatBotResponse.addQuickButton(i+"개",ButtonAction.블럭이동,"68df93d62c0d3f5ee7182eb2",ButtonParamKey.product,quantityDto);
            }


            chatBotResponse.addCommerceCard(commerceCard);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "delivery")
    public ChatBotResponse delivery(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            ProductDto product = chatBotRequest.getProduct();

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }
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

            Button defaultAddressButton = new Button("선택하기",ButtonAction.블럭이동,"68df7a1f2c0d3f5ee7182c35");
            defaultAddressButton.setExtra(ButtonParamKey.product,product);
            defaultAddressButton.setExtra(ButtonParamKey.address,defaultAddress);

            TextCard defaultTextCard = new TextCard();
            defaultTextCard.setDescription("[기본 배송지]\n\n"+defaultAddress.getFullAddress());
            defaultTextCard.setButtons(defaultAddressButton);

            Button addAddressButton = new Button("직접 입력하기",ButtonAction.블럭이동,"68df9df4edb87047afde88e8");
            addAddressButton.setExtra(ButtonParamKey.product,product);

            TextCard textCard = new TextCard();
            textCard.setDescription("직접 배송지를 입력 할 수 있습니다.");
            textCard.setButtons(addAddressButton);

            Carousel carousel = new Carousel();
            carousel.addComponent(defaultTextCard);
            carousel.addComponent(textCard);

            chatBotResponse.addCarousel(carousel);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "input/delivery")
    public ChatBotResponse inputDelivery(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String delivery = chatBotRequest.getDelivery();
            ProductDto product = chatBotRequest.getProduct();

            AddressDto newAddressDto = AddressDto.builder()
                    .fullAddress(delivery)
                    .build();

            TextCard textCard = new TextCard();
            textCard.setDescription(delivery+"\n\n해당 배송지로 진행하시겠습니까?");


            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("처음으로",ButtonAction.블럭이동,"68de38ae47a9e61d1ae66a4f");
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"68df9df4edb87047afde88e8",ButtonParamKey.product,product);


            Button orderSheetButton = new Button("진행하기",ButtonAction.블럭이동,"68df7a1f2c0d3f5ee7182c35",ButtonParamKey.product,product);
            orderSheetButton.setExtra(ButtonParamKey.address,newAddressDto);

            chatBotResponse.addQuickButton(orderSheetButton);

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

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }

            UserDto userDto = maybeUser.get();

            ProductDto product = chatBotRequest.getProduct();
            AddressDto addressDto = chatBotRequest.getAddressDto();

            TextCard textCard = new TextCard();
            textCard.setDescription("아래 내용으로 주문을 진행하시겠습니까?");

            TextCard delivery = new TextCard();
            delivery.setTitle("배송지");
            delivery.setDescription(userDto.getDefaultAddress().getFullAddress());

            int totalPrice = product.getDiscountedPrice() * product.getQuantity();

            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.setProfile(Map.of(
                    "title",profile.getNickname(),
                    "imageUrl",profile.getImageUrl()
            ));
            itemCard.setThumbnail(new Thumbnail(product.getImageUrl()));
            itemCard.addItemList("상품명",product.getName());
            itemCard.addItemList("개당 가격",String.format("%,d",product.getDiscountedPrice())+"원");
            itemCard.addItemList("수량",product.getQuantity()+"개");
            itemCard.setSummary("총 결제금액",String.format("%,d",totalPrice)+"원");

            itemCard.setTitle("배송지");
            itemCard.setDescription(addressDto.getFullAddress());

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addItemCard(itemCard);
//            chatBotResponse.addTextCard(delivery);
            chatBotResponse.addQuickButton("처음으로",ButtonAction.블럭이동,"68de38ae47a9e61d1ae66a4f");

            Button orderButton = new Button("주문하기",ButtonAction.블럭이동,"68df7bdc5390541970472535",ButtonParamKey.product,product);
            orderButton.setExtra(ButtonParamKey.address,addressDto);

            chatBotResponse.addQuickButton(orderButton);

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

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }

            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            ProductDto product = chatBotRequest.getProduct();
            AddressDto addressDto = chatBotRequest.getAddressDto();
            UserDto userDto = maybeUser.get();
            BranchDto branch = branchService.getBranch(chatBotRequest.getBot().getId());
            OrderDto orderDto = orderService.orderProduct(product, userDto, addressDto);
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
                    .append("\n")
                    .append("총 결제금액:\n\n"+ StringFormatterUtil.formatCurrency(String.valueOf(orderDto.getTotalPrice()))+"원")
                    .append("\uD83D\uDCE6 배송 안내\n")
                    .append("입금 확인 후 1~2일 이내에 우편함으로 배송되며, 배송 완료 시 문자로 안내드립니다.\n")
                    .append("*상품은 기본적으로 우편함으로 배송되며, 수량이 많을 경우 문 앞까지 배송됩니다.\n\n")
                    .append("\uD83D\uDCB0 입금 기한 안내 및 유의사항\n")
                    .append("주문일로부터 24시간 이내 미입금 시 주문은 자동 취소됩니다.\n")
                    .append("입금 시, 입금자명과 주문자명(회원명)이 다를 경우 확인이 지연될 수 있으니 유의해 주세요.");

            textCard.setDescription(sb.toString());
            chatBotResponse.addTextCard(textCard);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("주문을 실패하였습니다.");
        }
    }
}
