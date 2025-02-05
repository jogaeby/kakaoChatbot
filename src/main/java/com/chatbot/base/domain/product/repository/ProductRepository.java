package com.chatbot.base.domain.product.repository;

import com.chatbot.base.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

}
