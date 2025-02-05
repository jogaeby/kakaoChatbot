package com.chatbot.base.domain.product.service.impl;

import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.service.MemberService;
import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.product.dto.ProductDTO;
import com.chatbot.base.domain.product.repository.ProductRepository;
import com.chatbot.base.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final MemberService memberService;

    @Override
    public List<ProductDTO> getProductList(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        return products.getContent().stream()
                .map(Product::toDTO)
                .sorted(Comparator.comparing(ProductDTO::getCreateDate).reversed()
                        .thenComparing(ProductDTO::getStatusPriority))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void addProduct(ProductDTO productDTO, String memberId) {
        Member member = memberService.getMemberById(memberId);
        Product product = Product.of(productDTO,member);

        if (!isFullDisplayProduct(LocalDateTime.now())) {
            product.updateStatus(ProductStatus.DISPLAY);
        }

        productRepository.save(product);
    }

    @Transactional
    @Override
    public void deleteProduct(String productId) {
        productRepository.deleteById(UUID.fromString(productId));
    }

    @Override
    public boolean isOwnerProduct(String productId, String memberId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new NoSuchElementException("Product not found / id = " + productId));
        if (product.getMember().getId().equals(memberId)) {
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public void updateProduct(ProductDTO productDTO) {
        String id = productDTO.getId();

        Product product = productRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("Product not found / id = " + id));

        product.update(productDTO);
    }

    @Transactional
    @Override
    public void updateProductStatus(LocalDateTime localDateTime) {
        LocalDateTime startDate = localDateTime.with(LocalTime.MIN);
        LocalDateTime endDate = localDateTime.with(LocalTime.MAX);
        log.info("상품 상태 업데이트 날짜 범위 {} ~ {}", startDate, endDate);

        // 상태 변경을 각 메서드로 분리 (순서 중요)
        updatePreDisplayToRegistration();
        updateDisplayToPreDisplay();
        updateRegistrationToDisplay(startDate, endDate);
    }

    private void updateRegistrationToDisplay(LocalDateTime startDate, LocalDateTime endDate) {
        List<Product> currentProducts = productRepository.findByStatusAndCreateDateBetween(ProductStatus.REGISTRATION, startDate, endDate);
        log.info("REGISTRATION -> DISPLAY 상태 변경 대상 수: {}", currentProducts.size());

        currentProducts.forEach(currentProduct -> {
            currentProduct.updateStatus(ProductStatus.DISPLAY);
        });

        productRepository.saveAll(currentProducts);  // 상태 변경 후 저장
    }

    private void updateDisplayToPreDisplay() {
        List<Product> displayProducts = productRepository.findByStatus(ProductStatus.DISPLAY);
        log.info("DISPLAY -> PRE_DISPLAY 상태 변경 대상 수: {}", displayProducts.size());

        displayProducts.forEach(displayProduct -> {
            displayProduct.updateStatus(ProductStatus.PRE_DISPLAY);
        });

        productRepository.saveAll(displayProducts);  // 상태 변경 후 저장
    }

    private void updatePreDisplayToRegistration() {
        List<Product> preDisplayProducts = productRepository.findByStatus(ProductStatus.PRE_DISPLAY);
        log.info("PRE_DISPLAY -> REGISTRATION 상태 변경 대상 수: {}", preDisplayProducts.size());

        preDisplayProducts.forEach(preDisplayProduct -> {
            preDisplayProduct.updateStatus(ProductStatus.REGISTRATION);
        });

        productRepository.saveAll(preDisplayProducts);  // 상태 변경 후 저장
    }

    private boolean isFullDisplayProduct(LocalDateTime date) {
        LocalDateTime startDate = date.with(LocalTime.MIN);
        LocalDateTime endDate = date.with(LocalTime.MAX);

        List<Product> displayProducts = productRepository.findByStatusAndCreateDateBetween(ProductStatus.DISPLAY, startDate, endDate);

        if (displayProducts.size() < 10) {
           return false;
        }
        return true;
    }
}
