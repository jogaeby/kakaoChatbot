package com.chatbot.base.domain.user.controller;

import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;
import com.chatbot.base.domain.user.service.UserService;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Profile;
import com.chatbot.base.dto.kakao.response.property.components.ItemCard;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;
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
@RequestMapping(value = "/kakao/chatbot/user")
public class KakaoUserController {
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    private final UserService userService;

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

            Profile profile = new Profile("프로필 정보","https://mblogthumb-phinf.pstatic.net/MjAxNjExMTFfMTUz/MDAxNDc4ODUwNjA5NzY0.N7CFXA8dkfH8II76uPIZupUCKQwwiYZO9oacqOx-ztAg.s0zfuN-xrH6GUzsmXSNscdiX2uRANwps_185X_qh7zQg.JPEG.alclsrorm/attachImage_1270282195.jpeg?type=w800");
            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");

            itemCard.setProfile(profile);
            itemCard.addItemList("이름",name);
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
            chatBotResponse.addQuickButton("다시입력", ButtonAction.블럭이동,"67e29aff64979267ce2aa236");
            chatBotResponse.addQuickButton("가입하기", ButtonAction.블럭이동,"68db4867a02e490d347cec28", ButtonParamKey.user,joinForm);

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

            UserDto join = userService.join(chatBotRequest.getBot().getName(), chatBotRequest.getUserKey(), joinForm.getName(), joinForm.getPhone(), defaultAddress.getFullAddress(),defaultAddress.isDefaultYn(), true);

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

            Optional<UserDto> maybeUser = userService.isUser(chatBotRequest.getUserKey());

            if (maybeUser.isPresent()) {
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

                Profile profile = new Profile();
                profile.setTitle("프로필 정보");
                ItemCard itemCard = new ItemCard();
                itemCard.setProfile(profile);
                itemCard.setItemListAlignment("right");

                itemCard.setProfile(profile);
                itemCard.addItemList("이름",userDto.getName());
                itemCard.addItemList("연락처",userDto.getPhone());
                itemCard.setTitle("기본 배송지");
                itemCard.setDescription(defaultAddressStr);


                chatBotResponse.addItemCard(itemCard);
                return chatBotResponse;
            }

            return chatBotExceptionResponse.createAuthException();
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }


}
