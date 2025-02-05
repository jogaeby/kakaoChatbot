package com.chatbot.base.domain.product.service;

import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductService {
    List<ProductDTO> getProductList(Pageable pageable);

    void addProduct(ProductDTO productDTO, String memberId);

    void deleteProduct(String productId);

    boolean isDeleteProduct(String productId, String memberId);

    void updateProductStatus(LocalDateTime localDateTime);
}
