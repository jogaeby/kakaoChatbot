package com.chatbot.base.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressDto {
    private String recipientName; // 수령인 이름

    private String recipientPhone; // 수령인 전화번호

    private String postalCode; // 우편번호

    private String roadAddress; // 도로명 주소

    private String detailAddress; // 상세 주소

    private String fullAddress;

    private boolean defaultYn; // 기본 배송지 여부
}
