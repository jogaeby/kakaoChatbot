package com.chatbot.base.controller.web;

import com.chatbot.base.common.HttpService;
import com.chatbot.base.domain.member.constant.MemberRole;
import com.chatbot.base.domain.member.dto.MemberDTO;
import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.dto.ProductDTO;
import com.chatbot.base.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("product")
public class ProductController {
    private final ProductService productService;
    private final HttpService httpService;

    @GetMapping("")
    public String getProductPage() {
        return "product";
    }
    @GetMapping("list")
    public ResponseEntity getProducts(Pageable pageable) {
        try {

            List<ProductDTO> productList = productService.getProductList(pageable);

            return ResponseEntity
                    .ok(productList);
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }

    @PostMapping()
    public ResponseEntity addProduct(@RequestBody ProductDTO productDTO) {
        try {
            MemberDTO member = httpService.getMemberDTOFromHttpRequest();

            productService.addProduct(productDTO,member.getId());

            return ResponseEntity
                    .ok()
                    .build();
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteProduct(@PathVariable String id) {
        try {
            if (httpService.isAdmin() || productService.isDeleteProduct(id,httpService.getMemberDTOFromHttpRequest().getId())) {

                productService.deleteProduct(id);

                return ResponseEntity
                        .ok()
                        .build();
            }else {
                throw new AuthenticationException("삭제 권한이 없습니다. productId = " + id);
            }
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }
}
