package com.chatbot.base.domain.product.service.impl;

import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.service.MemberService;
import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.product.dto.ProductDTO;
import com.chatbot.base.domain.product.repository.ProductRepository;
import com.chatbot.base.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final MemberService memberService;

    @Override
    public List<ProductDTO> getProductList(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        return products.getContent().stream()
                .map(Product::toDTO)
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
    public boolean isDeleteProduct(String productId, String memberId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new NoSuchElementException("Product not found / id = " + productId));
        if (product.getMember().getId().equals(memberId)) {
            return true;
        }
        return false;
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
