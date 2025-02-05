package com.chatbot.base.domain.product.service;

import com.chatbot.base.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> getProductList(Pageable pageable);
}
