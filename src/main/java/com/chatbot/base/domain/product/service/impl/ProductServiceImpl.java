package com.chatbot.base.domain.product.service.impl;

import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.repository.ProductRepository;
import com.chatbot.base.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Override
    public Page<Product> getProductList(Pageable pageable) {
        return productRepository.findAll(pageable);

    }
}
