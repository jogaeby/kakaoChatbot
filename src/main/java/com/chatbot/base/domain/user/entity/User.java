package com.chatbot.base.domain.user.entity;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.cart.entity.Cart;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.AddressDto;
import com.chatbot.base.domain.user.dto.UserDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
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
    private String channelId;

    @Column(nullable = false)
    private boolean privacyAgreed;

    @Column(nullable = false)
    private LocalDateTime privacyAgreedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    // 한 명의 유저가 여러 배송지를 가질 수 있도록
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id") // Address 테이블에 user_id FK 생성
    private List<Address> addresses = new ArrayList<>();

    @Builder
    public User(String channelName, String channelId, String userKey, String name, String phone, boolean privacyAgreed, LocalDateTime privacyAgreedAt, List<Address> addresses, Cart cart) {
        this.userKey = userKey;
        this.name = name;
        this.phone = phone;
        this.channelName = channelName;
        this.channelId = channelId;
        this.privacyAgreed = privacyAgreed;
        this.privacyAgreedAt = privacyAgreedAt;
        this.addresses = addresses;
        this.cart = cart;
    }

    public static User create(String channelName, String channelId, String userKey, String name, String phone, String fullAddress, boolean isDefault,boolean privacyAgreed) {

        Address address = Address.create(fullAddress, isDefault);

        User user = User.builder()
                .userKey(userKey)
                .name(name)
                .phone(phone)
                .channelName(channelName)
                .channelId(channelId)
                .addresses(new ArrayList<>(List.of(address)))
                .privacyAgreed(privacyAgreed)
                .privacyAgreedAt(LocalDateTime.now())
                .build();

        // ✅ 장바구니 초기화
        Cart cart = Cart.create(user);
        user.setCart(cart); // 연관관계 설정

        return user;
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
                .cart(cart.toDto())
                .build();
    }

    private void setCart(Cart cart) {
        this.cart = cart;
        if (cart.getUser() != this) {
            cart.setUser(this);
        }
    }

    public void modifyDefaultAddress(String address) {
        // 1. 현재 기본 배송지 있는지 확인
        Optional<Address> maybeDefaultAddress = addresses.stream()
                .filter(Address::isDefault)
                .findFirst();

        if (maybeDefaultAddress.isPresent()) {
            // 2. 기존 기본 배송지 업데이트
            Address defaultAddress = maybeDefaultAddress.get();
            defaultAddress.updateFullAddress(address); // Address 엔티티에 update 메서드 필요
        } else {
            // 3. 기본 배송지 없으면 새로 생성
            Address newDefaultAddress = Address.create(address, true);

            // 다른 배송지들은 기본 아님 처리
            addresses.forEach(addr -> addr.updateDefaultStatus(false));

            addresses.add(newDefaultAddress);
        }
    }

    public void modifyName(String name) {
        this.name = name;
    }

    public void modifyPhone(String phone) {
        this.phone = phone;
    }

    public void addProductToCart(ProductDto productDto) {
        this.cart.getCartItems().add(productDto);
    }
}
