package com.chatbot.base.controller.web;

import com.chatbot.base.annotation.PassAuth;
import com.chatbot.base.common.HttpService;
import com.chatbot.base.domain.member.constant.MemberRole;
import com.chatbot.base.domain.member.dto.MemberDTO;
import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.product.dto.ProductDTO;
import com.chatbot.base.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PassAuth
    @GetMapping("realEstate")
    public String getPublicProductPage() {
        return "realEstate";
    }

    @GetMapping("list")
    public ResponseEntity getProducts(Pageable pageable) {
        try {
            MemberDTO memberDTOFromHttpRequest = httpService.getMemberDTOFromHttpRequest();

            if (httpService.isAdmin()) {
                List<ProductDTO> productList = productService.getProductList(pageable);
                return ResponseEntity
                        .ok(productList);
            }

            List<ProductDTO> productList = productService.getProductListByMember(pageable,memberDTOFromHttpRequest.getId());

            return ResponseEntity
                    .ok(productList);
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }

    @PassAuth
    @GetMapping("previous")
    public ResponseEntity getPreviousProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProductDTO> productList = productService.getProductList(ProductStatus.PRE_DISPLAY,page,size);

            return ResponseEntity
                    .ok(productList);
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }

    @PassAuth
    @GetMapping("{id}")
    public ResponseEntity getPreviousProduct(@PathVariable String id) {
        try {
            ProductDTO productDTO = productService.getProduct(id);

            return ResponseEntity
                    .ok(productDTO);
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }
    @PassAuth
    @GetMapping("search")
    public ResponseEntity getPreviousProducts(@RequestParam String input, @RequestParam String category) {
        try {
            List<ProductDTO> productDTOS = productService.searchProducts(category, input);
            return ResponseEntity
                    .ok(productDTOS);
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @ModelAttribute ProductDTO productDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            MemberDTO member = httpService.getMemberDTOFromHttpRequest();

            // 서비스 계층에서 imageFile과 productDTO를 함께 처리하도록 수정해야 합니다.
            productService.addProduct(productDTO, member.getId(),imageFile);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }

    @PatchMapping()
    public ResponseEntity updateProduct(
            @ModelAttribute ProductDTO productDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
    {
        try {
            if (httpService.isAdmin() || productService.isOwnerProduct(productDTO.getId(),httpService.getMemberDTOFromHttpRequest().getId())) {

                productService.updateProduct(productDTO,imageFile);

                return ResponseEntity
                        .ok()
                        .build();
            }else {
                throw new AuthenticationException("삭제 권한이 없습니다. productId = " + productDTO.getId());
            }
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
            if (httpService.isAdmin() || productService.isOwnerProduct(id,httpService.getMemberDTOFromHttpRequest().getId())) {

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
