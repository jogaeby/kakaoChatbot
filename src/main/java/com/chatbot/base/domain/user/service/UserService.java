package com.chatbot.base.domain.user.service;

import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.AccountDto;
import com.chatbot.base.domain.user.dto.UserDto;

import java.util.Optional;

public interface UserService {
    UserDto join(String channelName, String channelId, String userKey, String name, String phone, String address, boolean addressDefault, boolean privacyAgreed);

    Optional<UserDto> isUser(String userKey);

    Optional<UserDto> isBlackUser(String userKey);

    UserDto modifyAddress(UserDto userDto, String address);

    UserDto modifyName(UserDto userDto, String name);

    UserDto modifyPhone(UserDto userDto, String phone);

    UserDto modifyAccount(UserDto userDto, AccountDto accountDto);
}
