package com.chatbot.base.domain.user.entity;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "users") // 테이블 이름 users로 지정
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String userKey;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String channelName;

    @Column(nullable = false)
    private boolean privacyAgreed;

    @Column(nullable = false)
    private LocalDateTime privacyAgreedAt;

    // 한 명의 유저가 여러 배송지를 가질 수 있도록
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id") // Address 테이블에 user_id FK 생성
    private List<Address> addresses = new ArrayList<>();

    @Builder
    public User(String channelName, String userKey, String name, String phone, boolean privacyAgreed, LocalDateTime privacyAgreedAt, List<Address> addresses) {
        this.userKey = userKey;
        this.name = name;
        this.phone = phone;
        this.channelName = channelName;
        this.privacyAgreed = privacyAgreed;
        this.privacyAgreedAt = privacyAgreedAt;
        this.addresses = addresses;
    }

    public static User create(String channelName, String userKey, String name, String phone, String fullAddress, boolean isDefault,boolean privacyAgreed) {
        Address address = Address.create(fullAddress,isDefault);
        return User.builder()
                .userKey(userKey)
                .name(name)
                .phone(phone)
                .channelName(channelName)
                .addresses(new ArrayList<>(List.of(address))) // ✅ 수정 가능 리스트
                .privacyAgreed(privacyAgreed)
                .privacyAgreedAt(LocalDateTime.now())
                .build();
    }

    public UserDto toDto() {
        List<AddressDto> addressDtos = Optional.ofNullable(this.addresses)
                .orElse(Collections.emptyList())
                .stream()
                .map(Address::toDto)
                .toList(); // ✅ 더 간결


        return UserDto.builder()
                .userKey(this.userKey)
                .name(this.name)
                .phone(this.phone)
                .channelName(this.channelName)
                .privacyAgreed(this.privacyAgreed)
                .privacyAgreedAt(this.privacyAgreedAt)
                .addressDtos(addressDtos)
                .build();
    }
}
