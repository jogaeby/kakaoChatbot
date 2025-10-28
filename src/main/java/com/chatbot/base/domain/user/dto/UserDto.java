package com.chatbot.base.domain.user.dto;

import com.chatbot.base.domain.cart.dto.CartDto;
import com.chatbot.base.domain.user.entity.Account;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class UserDto {
    private String userKey;

    private String name;

    private String phone;

    private String channelName;

    private boolean privacyAgreed;

    private LocalDateTime privacyAgreedAt;

    private CartDto cart;

    private AccountDto account;

    @Builder.Default
    private List<AddressDto> addressDtos = new ArrayList<>();

    public AddressDto getDefaultAddress() {
        return addressDtos.stream()
                .filter(AddressDto::isDefaultYn)
                .findFirst()
                .orElse(null);
    }

}
