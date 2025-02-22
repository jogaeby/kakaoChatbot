package com.chatbot.base.domain.product.service.impl;

import com.chatbot.base.common.ImageUtil;
import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.service.MemberService;
import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.ProductSpecification;
import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.product.dto.ProductDTO;
import com.chatbot.base.domain.product.repository.ProductRepository;
import com.chatbot.base.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final MemberService memberService;
    private final ImageUtil imageUtil;

    @Override
    public List<ProductDTO> getProductListByMember(Pageable pageable, String memberId) {
        Member member = memberService.getMemberById(memberId);

        Page<Product> products = productRepository.findAllByMember(member,pageable);

        return products.getContent().stream()
                .map(Product::toDTO)
                .sorted(Comparator.comparing(ProductDTO::getCreateDate).reversed()
                        .thenComparing(ProductDTO::getStatusPriority))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductList(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        return products.getContent().stream()
                .map(Product::toDTO)
                .sorted(Comparator.comparing(ProductDTO::getCreateDate).reversed()
                        .thenComparing(ProductDTO::getStatusPriority))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProducts(String category, String input, ProductStatus status) {
        Specification<Product> spec = ProductSpecification.withDynamicQuery(category, input)
                .and((root, query, cb) -> cb.equal(root.get("status"), status));

        List<Product> products = productRepository.findAll(spec);

        return products.stream()
                .map(Product::toDTO)
                .sorted(Comparator.comparing(ProductDTO::getCreateDate).reversed()
                        .thenComparing(ProductDTO::getStatusPriority))
                .collect(Collectors.toList());
    }


    @Override
    public Page<ProductDTO> getProductList(ProductStatus status, int pageNum, int pageSize) {
        Sort sort = Sort.by("saleDate");
        Pageable pageable = PageRequest.of(pageNum, pageSize,sort);
        return productRepository.findByStatusAndSaleDateGreaterThanEqual(status, LocalDate.now(),pageable)
                .map(Product::toDTO);
    }

    @Override
    public ProductDTO getProduct(String productId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new NoSuchElementException("not found Product / id = " + productId));
        return product.toDTO();
    }

    @Transactional
    @Override
    public void addProduct(ProductDTO productDTO, String memberId, MultipartFile imageFile) {
        Member member = memberService.getMemberById(memberId);
        Product product = Product.of(productDTO, member);
        productRepository.save(product);

        // 이미지 파일이 존재하면 저장 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String savedFileUrl = imageUtil.saveFile(imageFile, product.getUuid().toString());
                product.addImageUrl(savedFileUrl);
            } catch (IOException e) {
                throw new RuntimeException("이미지 파일 저장 중 오류 발생", e);
            }
        }


    }

    @Transactional
    @Override
    public void deleteProduct(String productId) {
        productRepository.deleteById(UUID.fromString(productId));
    }

    @Override
    public boolean isOwnerProduct(String productId, String memberId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new NoSuchElementException("Product not found / id = " + productId));
        if (product.getMember().getId().equals(memberId)) {
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public void updateProduct(ProductDTO productDTO, MultipartFile imageFile) {
        String id = productDTO.getId();

        Product product = productRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("Product not found / id = " + id));
        product.update(productDTO);

        // 이미지 파일이 존재하면 저장 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String savedFileUrl = imageUtil.saveFile(imageFile, product.getUuid().toString());
                product.updateImageUrls(savedFileUrl);
            } catch (IOException e) {
                throw new RuntimeException("이미지 파일 저장 중 오류 발생", e);
            }
        }
    }

    @Transactional
    @Override
    public void updateProductStatus(LocalDate localDate) {

        // 상태 변경을 각 메서드로 분리 (순서 중요)
        updateDisplayToPreDisplay();
        updateRegistrationToDisplay(localDate);
    }

    private void updateRegistrationToDisplay(LocalDate localDate) {
        List<Product> currentProducts = productRepository.findByStatusAndDisplayDate(ProductStatus.REGISTRATION,localDate);
        log.info("REGISTRATION -> DISPLAY 상태 변경 대상 수: {}", currentProducts.size());

        currentProducts.forEach(currentProduct -> {
            currentProduct.updateStatus(ProductStatus.DISPLAY);
        });

        productRepository.saveAll(currentProducts);  // 상태 변경 후 저장
    }

    private void updateDisplayToPreDisplay() {
        List<Product> displayProducts = productRepository.findByStatus(ProductStatus.DISPLAY);
        log.info("DISPLAY -> PRE_DISPLAY 상태 변경 대상 수: {}", displayProducts.size());

        displayProducts.forEach(displayProduct -> {
            displayProduct.updateStatus(ProductStatus.PRE_DISPLAY);
        });

        productRepository.saveAll(displayProducts);  // 상태 변경 후 저장
    }

    private void updatePreDisplayToRegistration() {
        List<Product> preDisplayProducts = productRepository.findByStatus(ProductStatus.PRE_DISPLAY);
        log.info("PRE_DISPLAY -> REGISTRATION 상태 변경 대상 수: {}", preDisplayProducts.size());

        preDisplayProducts.forEach(preDisplayProduct -> {
            preDisplayProduct.updateStatus(ProductStatus.REGISTRATION);
        });

        productRepository.saveAll(preDisplayProducts);  // 상태 변경 후 저장
    }
}
