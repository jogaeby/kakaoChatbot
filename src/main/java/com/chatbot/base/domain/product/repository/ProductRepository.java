package com.chatbot.base.domain.product.repository;

import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.constant.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    List<Product> findByStatusAndDisplayDateBetween(ProductStatus status, LocalDateTime startDate, LocalDateTime endDate);

    List<Product> findByStatus(ProductStatus status);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByStatusAndSaleDateGreaterThanEqual(ProductStatus status, LocalDate today, Pageable pageable);

    Page<Product> findAllByMember(Member member, Pageable pageable);

}
