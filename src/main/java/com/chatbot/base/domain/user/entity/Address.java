package com.chatbot.base.domain.user.entity;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.user.dto.AddressDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "addresses")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {
    @Column(nullable = true)
    private String recipientName; // 수령인 이름

    @Column(nullable = true)
    private String recipientPhone; // 수령인 전화번호

    @Column(nullable = true)
    private String postalCode; // 우편번호

    @Column(nullable = true)
    private String roadAddress; // 도로명 주소

    @Column(nullable = true)
    private String detailAddress; // 상세 주소
    @Column(nullable = false)
    private String fullAddress;

    @Column(nullable = false)
    private boolean isDefault; // 기본 배송지 여부

    @Builder
    public Address(String recipientName, String recipientPhone, String postalCode, String roadAddress, String detailAddress, String fullAddress, boolean isDefault) {
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.postalCode = postalCode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.fullAddress = fullAddress;
        this.isDefault = isDefault;
    }

    public static Address create(String fullAddress, boolean isDefault) {
        return Address.builder()
                .fullAddress(fullAddress)
                .isDefault(isDefault)
                .build();
    }

    public AddressDto toDto() {
        return AddressDto.builder()
                .fullAddress(this.fullAddress)
                .defaultYn(this.isDefault)
                .build();
    }
}
