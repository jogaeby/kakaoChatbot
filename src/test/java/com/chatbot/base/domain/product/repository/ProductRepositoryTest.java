package com.chatbot.base.domain.product.repository;

import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.constant.ProductStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    @Test
    void findByStatusAndCreateDateBetween() {
    }

    @Test
    void findByStatus() {
        List<Product> byStatus = productRepository.findByStatus(ProductStatus.PRE_DISPLAY);
        System.out.println("byStatus = " + byStatus.size());
    }

    @Test
    void testFindByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> byStatus = productRepository.findByStatus(ProductStatus.PRE_DISPLAY,pageable).getContent();
        System.out.println("byStatus = " + byStatus.size());
    }
}