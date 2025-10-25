package com.chatbot.base.domain.cart.service.impl;

import com.chatbot.base.domain.cart.service.CartService;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.UserDto;
import com.chatbot.base.domain.user.entity.User;
import com.chatbot.base.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto addProductToCart(String userKey, ProductDto productDto) {
        User user = userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ProductDto> cartItems = user.getCart().getCartItems();

        // ✅ 동일 상품 존재 여부 확인
        Optional<ProductDto> existingItemOpt = cartItems.stream()
                .filter(item -> item.getId().equals(productDto.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // ✅ 이미 있는 상품이면 수량 변경
            ProductDto existingItem = existingItemOpt.get();
            existingItem.setQuantity(productDto.getQuantity());
        } else {
            // ✅ 새로운 상품 추가
            user.addProductToCart(productDto);
        }

        return user.toDto();
    }

    @Transactional
    @Override
    public UserDto deleteProductToCart(String userKey, String productId) {
        User user = userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ProductDto> cartItems = user.getCart().getCartItems();

        // ✅ 삭제 대상 상품 찾기
        boolean removed = cartItems.removeIf(item -> item.getId().equals(productId));

        return user.toDto();
    }

    @Transactional
    @Override
    public UserDto deleteProductsToCart(String userKey, Set<String> productIds) {
        User user = userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ProductDto> cartItems = user.getCart().getCartItems();

        // ✅ productIds에 해당하는 상품 모두 제거
        cartItems.removeIf(item -> productIds.contains(item.getId()));

        return user.toDto();
    }
}
