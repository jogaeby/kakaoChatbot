package com.chatbot.base.controller.web;

import com.chatbot.base.domain.member.constant.MemberRole;
import com.chatbot.base.domain.member.dto.MemberDTO;
import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("product")
public class ProductController {
    private final ProductService productService;

    @GetMapping("")
    public String getProductPage() {
        return "product";
    }
    @GetMapping("list")
    public ResponseEntity getProducts(Pageable pageable)
    {
        try {

            Page<Product> productList = productService.getProductList(pageable);

            return ResponseEntity
                    .ok(productList.getContent());
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }

    @PostMapping()
    public ResponseEntity addProduct(Pageable pageable)
    {
        try {

            Page<Product> productList = productService.getProductList(pageable);

            return ResponseEntity
                    .ok(productList.getContent());
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }
}
