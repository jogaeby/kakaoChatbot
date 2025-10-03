package com.chatbot.base.domain.user.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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

    private List<AddressDto> addressDtos;

    public AddressDto getDefaultAddress() {
        return addressDtos.stream()
                .filter(AddressDto::isDefaultYn)
                .findFirst()
                .orElse(null);
    }

}
