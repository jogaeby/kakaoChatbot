package com.chatbot.base.domain.product.repository;

import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.constant.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByStatusAndCreateDateBetween(ProductStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
