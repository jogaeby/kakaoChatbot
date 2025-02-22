package com.chatbot.base.domain.product.service;

import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.product.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductService {
    List<ProductDTO> getProductListByMember(Pageable pageable,String memberId);

    List<ProductDTO> getProductList(Pageable pageable);

    List<ProductDTO> searchProducts(String category, String input);

    Page<ProductDTO> getProductList(ProductStatus status, int pageNum,int pageSize);

    ProductDTO getProduct(String productId);

    void addProduct(ProductDTO productDTO, String memberId, MultipartFile imageFile);

    void deleteProduct(String productId);

    boolean isOwnerProduct(String productId, String memberId);

    void updateProduct(ProductDTO productDTO);

    void updateProductStatus(LocalDateTime localDateTime);
}
