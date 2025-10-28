package com.chatbot.base.domain.user.controller;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.cart.dto.CartDto;
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
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.CommerceCard;
import com.chatbot.base.dto.kakao.response.property.components.ItemCard;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/user")
public class KakaoUserController {
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    private final UserService userService;

    private final OrderService orderService;

    @PostMapping(value = "join/double-check")
    public ChatBotResponse joinDoubleCheck(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isPresent()) {
                TextCard textCard = new TextCard();
                textCard.setDescription("이미 가입된 회원입니다.");
                chatBotResponse.addTextCard(textCard);
                return chatBotResponse;
            }


            String name = chatBotRequest.getName();
            String phone = chatBotRequest.getPhone();
            String address = chatBotRequest.getAddress();

            TextCard textCard = new TextCard();
            textCard.setDescription("해당 정보로 간편가입을 진행하시겠습니까?");

            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");

            itemCard.addItemList("이름(입금자명)",name);
            itemCard.addItemList("연락처",phone);
            itemCard.setTitle("기본 배송지");
            itemCard.setDescription(address);


            UserDto joinForm = UserDto.builder()
                    .name(name)
                    .phone(phone)
                    .addressDtos(List.of(AddressDto.builder()
                            .fullAddress(address)
                            .defaultYn(true)
                            .build()))
                    .build();

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addQuickButton("다시입력", ButtonAction.블럭이동,"68de3871539054197046e5b2");
            chatBotResponse.addQuickButton("가입하기", ButtonAction.블럭이동,"68de386847a9e61d1ae66a39", ButtonParamKey.user,joinForm);
            StringFormatterUtil.objectToString(chatBotResponse);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "join")
    public ChatBotResponse join(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            UserDto joinForm = chatBotRequest.getUser();

            AddressDto defaultAddress = joinForm.getAddressDtos().stream()
                    .filter(AddressDto::isDefaultYn)
                    .findFirst()
                    .orElseThrow(Exception::new);

            UserDto join = userService.join(chatBotRequest.getBot().getName(), chatBotRequest.getBot().getId(), chatBotRequest.getUserKey(), joinForm.getName(), joinForm.getPhone(), defaultAddress.getFullAddress(),defaultAddress.isDefaultYn(), true);

            TextCard textCard = new TextCard();
            textCard.setDescription("성공적으로 간편가입을 완료했습니다.");
            chatBotResponse.addTextCard(textCard);
            return chatBotResponse;
        } catch (DuplicateKeyException e) {
            return chatBotExceptionResponse.createTextCardException("이미 가입된 회원입니다.");
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "info")
    public ChatBotResponse info(@RequestBody ChatBotRequest chatBotRequest) {
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
                List<AddressDto> addressDtos = userDto.getAddressDtos();
                // 기본 배송지 찾기 (없으면 Optional.empty)
                Optional<AddressDto> defaultAddress = addressDtos.stream()
                        .filter(AddressDto::isDefaultYn)
                        .findFirst();

                // 기본 배송지를 문자열로 추출, 없으면 "설정안됨"
                String defaultAddressStr = defaultAddress
                        .map(AddressDto::getFullAddress) // AddressDto에서 전체 주소를 가져오는 메서드 사용
                        .orElse("설정안됨");

                ItemCard infoItemCard = createInfoItemCard(userDto, defaultAddressStr);
                ItemCard accountInfoItemCard = createAccountInfoItemCard(userDto);
                carousel.addComponent(infoItemCard);
                carousel.addComponent(accountInfoItemCard);

                chatBotResponse.addCarousel(carousel);
                StringFormatterUtil.objectToString(chatBotResponse);
                return chatBotResponse;
            }

            return chatBotExceptionResponse.createAuthException();
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "input/delivery")
    public ChatBotResponse inputDeliveryUpdate(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }

            AddressDto defaultAddress = maybeUser.get().getDefaultAddress();

            String address = chatBotRequest.getAddress();

            AddressDto newAddress = AddressDto.builder()
                    .fullAddress(address)
                    .build();

            TextCard textCard = new TextCard();
            textCard.setDescription("[기존]\n"+defaultAddress.getFullAddress()+"\n\n[변경]\n"+address+"\n\n기본 배송지를 변경하시겠습니까?");

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("아니요",ButtonAction.블럭이동,"68de387a2c0d3f5ee717ece3");
            chatBotResponse.addQuickButton("변경하기",ButtonAction.블럭이동,"68dfa537465dc163a63cbfe8",ButtonParamKey.address,newAddress);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "update/delivery")
    public ChatBotResponse updateDeliveryUpdate(@RequestBody ChatBotRequest chatBotRequest) {
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

            Carousel carousel = new Carousel();
            UserDto userDto = maybeUser.get();

            AddressDto addressDto = chatBotRequest.getAddressDto();

            UserDto updateUserDto = userService.modifyAddress(userDto, addressDto.getFullAddress());

            List<AddressDto> addressDtos = updateUserDto.getAddressDtos();
            // 기본 배송지 찾기 (없으면 Optional.empty)
            Optional<AddressDto> defaultAddress = addressDtos.stream()
                    .filter(AddressDto::isDefaultYn)
                    .findFirst();

            // 기본 배송지를 문자열로 추출, 없으면 "설정안됨"
            String defaultAddressStr = defaultAddress
                    .map(AddressDto::getFullAddress) // AddressDto에서 전체 주소를 가져오는 메서드 사용
                    .orElse("설정안됨");

            ItemCard infoItemCard = createInfoItemCard(updateUserDto, defaultAddressStr);
            ItemCard accountInfoItemCard = createAccountInfoItemCard(updateUserDto);
            carousel.addComponent(infoItemCard);
            carousel.addComponent(accountInfoItemCard);

            chatBotResponse.addSimpleText("성공적으로 기본 배송지를 변경하였습니다.");
            chatBotResponse.addCarousel(carousel);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "input/name")
    public ChatBotResponse inputNameUpdate(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }
            String newName = chatBotRequest.getName();
            UserDto userDto = maybeUser.get();

            UserDto updateUserName = UserDto.builder()
                    .name(newName)
                    .build();

            TextCard textCard = new TextCard();
            textCard.setDescription("[기존]\n"+userDto.getName()+"\n\n[변경]\n"+newName+"\n\n이름(입금자명)을 변경하시겠습니까?");



            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("아니요",ButtonAction.블럭이동,"68de387a2c0d3f5ee717ece3");
            chatBotResponse.addQuickButton("변경하기",ButtonAction.블럭이동,"68f5cbc4b85f48088da44de6",ButtonParamKey.user,updateUserName);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "update/name")
    public ChatBotResponse updateNameUpdate(@RequestBody ChatBotRequest chatBotRequest) {
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

            Carousel carousel = new Carousel();
            UserDto userDto = maybeUser.get();
            UserDto newName = chatBotRequest.getUser();


            UserDto updateUserDto = userService.modifyName(userDto, newName.getName());

            List<AddressDto> addressDtos = updateUserDto.getAddressDtos();
            // 기본 배송지 찾기 (없으면 Optional.empty)
            Optional<AddressDto> defaultAddress = addressDtos.stream()
                    .filter(AddressDto::isDefaultYn)
                    .findFirst();

            // 기본 배송지를 문자열로 추출, 없으면 "설정안됨"
            String defaultAddressStr = defaultAddress
                    .map(AddressDto::getFullAddress) // AddressDto에서 전체 주소를 가져오는 메서드 사용
                    .orElse("설정안됨");

            ItemCard infoItemCard = createInfoItemCard(updateUserDto, defaultAddressStr);
            ItemCard accountInfoItemCard = createAccountInfoItemCard(updateUserDto);
            carousel.addComponent(infoItemCard);
            carousel.addComponent(accountInfoItemCard);

            chatBotResponse.addSimpleText("성공적으로 이름(입금자명)을 변경하였습니다.");
            chatBotResponse.addCarousel(carousel);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "input/phone")
    public ChatBotResponse inputPhoneUpdate(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isEmpty()) {
                return chatBotExceptionResponse.createAuthException();
            }
            String newPhone = chatBotRequest.getPhone();
            UserDto userDto = maybeUser.get();

            UserDto updateUserPhone = UserDto.builder()
                    .phone(newPhone)
                    .build();

            TextCard textCard = new TextCard();
            textCard.setDescription("[기존]\n"+userDto.getPhone()+"\n\n[변경]\n"+newPhone+"\n\n연락처를 변경하시겠습니까?");



            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("아니요",ButtonAction.블럭이동,"68de387a2c0d3f5ee717ece3");
            chatBotResponse.addQuickButton("변경하기",ButtonAction.블럭이동,"68f5cbd0edb87047afe27ab2",ButtonParamKey.user,updateUserPhone);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "update/phone")
    public ChatBotResponse updatePhoneUpdate(@RequestBody ChatBotRequest chatBotRequest) {
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
            Carousel carousel = new Carousel();

            UserDto userDto = maybeUser.get();
            UserDto newName = chatBotRequest.getUser();


            UserDto updateUserDto = userService.modifyPhone(userDto, newName.getPhone());

            List<AddressDto> addressDtos = updateUserDto.getAddressDtos();
            // 기본 배송지 찾기 (없으면 Optional.empty)
            Optional<AddressDto> defaultAddress = addressDtos.stream()
                    .filter(AddressDto::isDefaultYn)
                    .findFirst();

            // 기본 배송지를 문자열로 추출, 없으면 "설정안됨"
            String defaultAddressStr = defaultAddress
                    .map(AddressDto::getFullAddress) // AddressDto에서 전체 주소를 가져오는 메서드 사용
                    .orElse("설정안됨");

            ItemCard infoItemCard = createInfoItemCard(updateUserDto, defaultAddressStr);
            ItemCard accountInfoItemCard = createAccountInfoItemCard(updateUserDto);
            carousel.addComponent(infoItemCard);
            carousel.addComponent(accountInfoItemCard);

            chatBotResponse.addSimpleText("성공적으로 연락처를 변경하였습니다.");
            chatBotResponse.addCarousel(carousel);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "orders")
    public ChatBotResponse getOrders(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            Optional<UserDto> blackUser = userService.isBlackUser(chatBotRequest.getUserKey());
            if (blackUser.isPresent()) {
                return chatBotExceptionResponse.createBlackUserException();
            }

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isPresent()) {
                UserDto userDto = maybeUser.get();
                List<OrderDto> orderList = orderService.getOrderList(userDto.getUserKey());

                if (orderList.isEmpty()) {
                    chatBotResponse.addTextCard("최근 주문내역이 존재하지 않습니다.");
                    return chatBotResponse;
                }

                Carousel carousel = new Carousel();


                orderList.forEach(orderDto -> {
                    List<ProductDto> products = orderDto.getProduct();
                    TextCard orderDetail = new TextCard();
                    StringBuilder description = new StringBuilder();

                    // 대표 상품명 + n개
                    String firstProductName = products.get(0).getName();
                    int remainingCount = products.size() - 1;
                    String productNameDisplay = firstProductName;
                    if (remainingCount > 0) {
                        productNameDisplay += " 외 " + remainingCount + "개";
                    }

                    description.append("[").append(orderDto.getId()).append("] ")
                            .append(" (").append(orderDto.getStatus()).append(")")
                            .append("\n\n")
                            .append("주문번호: ").append(orderDto.getId())
                            .append("\n")
                            .append("상품명: ").append(productNameDisplay)
                            .append("\n")
                            .append("총 수량: ").append(orderDto.getTotalQuantity()).append("개")
                            .append("\n")
                            .append("총 결제금액: ")
                            .append(StringFormatterUtil.formatCurrency(String.valueOf(orderDto.getTotalPrice()))).append("원")
                            .append("\n")
                            .append("주문일자: ")
                            .append(orderDto.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                            .append("\n")
                            .append("상태: ").append(orderDto.getStatus());

                    orderDetail.setDescription(description.toString());
                    carousel.addComponent(orderDetail);
                });


                chatBotResponse.addTextCard("최근 주문내역");
                chatBotResponse.addCarousel(carousel);
                return chatBotResponse;
            }

            return chatBotExceptionResponse.createAuthException();
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }


    public ItemCard createInfoItemCard(UserDto userDto, String defaultAddressStr) {
        ItemCard itemCard = new ItemCard();
        itemCard.setItemListAlignment("right");

        itemCard.setProfile(Map.of(
                "title","프로필 정보",
                "imageUrl","https://pointman-file-repository.s3.ap-northeast-2.amazonaws.com/image/profile/icon-friends-ryan.png")
        );
        itemCard.addItemList("고유번호",userDto.getUserKey());
        itemCard.addItemList("이름(입금자명)",userDto.getName());
        itemCard.addItemList("연락처",userDto.getPhone());
        itemCard.setTitle("기본 배송지");
        itemCard.setDescription(defaultAddressStr);
        itemCard.setButtonLayout("vertical");

        itemCard.addButton(new Button("이름(입금자명) 변경하기",ButtonAction.블럭이동,"68f5cbbdedb87047afe27aaf"));
        itemCard.addButton(new Button("연락처 변경하기",ButtonAction.블럭이동,"68f5cbca465dc163a640d34e"));
        itemCard.addButton(new Button("기본 배송지 변경하기",ButtonAction.블럭이동,"68dfa315b85f48088da03587"));

        return itemCard;
    }

    public ItemCard createAccountInfoItemCard(UserDto userDto) {
        ItemCard itemCard = new ItemCard();
        itemCard.setItemListAlignment("right");
        itemCard.setProfile(Map.of(
                "title","프로필 정보",
                "imageUrl","https://pointman-file-repository.s3.ap-northeast-2.amazonaws.com/image/profile/icon-friends-ryan.png")
        );
        if (userDto.getAccount().isDefault()) {
            itemCard.addItemList("예금주",userDto.getAccount().getAccountName());
            itemCard.addItemList("은행명",userDto.getAccount().getBankName());
            itemCard.setTitle("계좌번호");
            itemCard.setDescription(userDto.getAccount().getAccountNumber());

            itemCard.addButton(new Button("환불계좌 변경하기",ButtonAction.블럭이동,"68f5cbbdedb87047afe27aaf"));

            return itemCard;
        }else {
            itemCard.addItemList("환불계좌","미등록");
            itemCard.setTitle("환불계좌 미등록 상태입니다.");
            itemCard.setDescription("아래 [환불계좌 등록하기] 버튼을 눌러 등록해주세요.");
            itemCard.addButton(new Button("환불계좌 등록하기",ButtonAction.블럭이동,"68f5cbbdedb87047afe27aaf"));
            return itemCard;
        }
    }

}

