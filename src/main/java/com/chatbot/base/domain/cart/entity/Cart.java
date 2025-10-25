package com.chatbot.base.domain.cart.entity;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.cart.converter.CartItemListConverter;
import com.chatbot.base.domain.cart.dto.CartDto;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "carts") // 테이블 이름 users로 지정
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Convert(converter = CartItemListConverter.class)
    @Column(columnDefinition = "TEXT") // JSON 문자열 저장
    private List<ProductDto> cartItems = new ArrayList<>();


    @Builder
    public Cart(User user, List<ProductDto> cartItems) {
        this.user = user;
        this.cartItems = cartItems;
    }

    public static Cart create(User user) {
        return Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CartDto toDto() {
        return CartDto.builder()
                .cartItems(cartItems)
                .build();
    }
}
