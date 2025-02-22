package com.chatbot.base.domain.product;

import com.chatbot.base.common.StringUtil;
import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.product.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "product")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    private static final Logger log = LoggerFactory.getLogger(Product.class);
    @Column(nullable = false)
    private String memo;

    @Column(nullable = false)
    private String no;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String currentPrice;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false)
    private String minPrice;

    @Column(nullable = false)
    private String expectedPrice;

    @Column(nullable = false)
    private LocalDate saleDate;

    @Column(nullable = false)
    private String managerName;

    @Column(nullable = false)
    private String managerPhone;

    @Lob
    private String description;

    private String link;

    // 이미지 URL 목록을 별도 테이블에 저장
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate displayDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Builder(access = AccessLevel.PRIVATE)
    public Product(String memo, String no, String category, String location, String price, String currentPrice,
                   String minPrice, LocalDate saleDate, String managerName, String managerPhone, String description,
                   String expectedPrice, String link, List<String> images, ProductStatus status, Member member,
                   LocalDate displayDate) {
        this.memo = memo;
        this.no = no;
        this.category = category;
        this.location = location;
        this.price = price;
        this.currentPrice = currentPrice;
        this.minPrice = minPrice;
        this.saleDate = saleDate;
        this.managerName = managerName;
        this.managerPhone = managerPhone;
        this.description = description;
        this.expectedPrice = expectedPrice;
        this.link = link;
        this.images = images != null ? images : new ArrayList<>();
        this.status = status;
        this.member = member;
        this.displayDate = displayDate;
    }

    /**
     * DTO와 Member 정보를 기반으로 Product 객체 생성.
     * displayDate와 오늘 날짜를 비교하여 ProductStatus를 결정함.
     */
    public static Product of(ProductDTO productDTO, Member member) {
        LocalDate displayDate = productDTO.getDisplayDate();
        ProductStatus status;
        int cmp = displayDate.compareTo(LocalDate.now());
        if (cmp == 0) {
            status = ProductStatus.DISPLAY;
        } else if (cmp > 0) {
            status = ProductStatus.REGISTRATION;
        } else {
            status = ProductStatus.PRE_DISPLAY;
        }

        return Product.builder()
                .memo(productDTO.getMemo())
                .no(productDTO.getNo())
                .category(productDTO.getCategory())
                .location(productDTO.getLocation())
                .price(productDTO.getPrice())
                .currentPrice(productDTO.getCurrentPrice())
                .minPrice(productDTO.getMinPrice())
                .expectedPrice(productDTO.getExpectedPrice())
                .saleDate(productDTO.getSaleDate())
                .managerName(productDTO.getManagerName())
                .managerPhone(StringUtil.formatPhoneNumber(productDTO.getManagerPhone()))
                .description(productDTO.getDescription())
                .link(productDTO.getLink())
                .images(productDTO.getImages())
                .status(status)
                .member(member)
                .displayDate(displayDate)
                .build();
    }

    /**
     * Product 엔티티를 DTO로 변환.
     */
    public ProductDTO toDTO() {
        return ProductDTO.builder()
                .id(getUuid().toString())
                .memo(memo)
                .no(no)
                .category(category)
                .location(location)
                .currentPrice(currentPrice)
                .price(price)
                .minPrice(minPrice)
                .expectedPrice(expectedPrice)
                .saleDate(saleDate)
                .managerName(managerName)
                .managerPhone(managerPhone)
                .description(description)
                .link(link)
                .images(images)
                .status(status.getName())
                .statusPriority(status.getPriority())
                .memberId(member.getId())
                .displayDate(displayDate)
                .createDate(getCreateDate().toLocalDate())
                .build();
    }

    /**
     * Product 정보를 업데이트.
     * displayDate가 오늘과 같으면 상태를 DISPLAY로 변경.
     */
    public void update(ProductDTO productDTO) {
        ProductStatus updatedStatus = productDTO.getDisplayDate().equals(LocalDate.now())
                ? ProductStatus.DISPLAY : this.status;
        log.error("@@@@@@@@@@@@@{}",productDTO.getImages());
        this.memo = productDTO.getMemo();
        this.no = productDTO.getNo();
        this.category = productDTO.getCategory();
        this.location = productDTO.getLocation();
        this.price = productDTO.getPrice();
        this.currentPrice = productDTO.getCurrentPrice();
        this.minPrice = productDTO.getMinPrice();
        this.expectedPrice = productDTO.getExpectedPrice();
        this.saleDate = productDTO.getSaleDate();
        this.managerName = productDTO.getManagerName();
        this.managerPhone = StringUtil.formatPhoneNumber(productDTO.getManagerPhone());
        this.description = productDTO.getDescription();
        this.link = productDTO.getLink();
        this.images = productDTO.getImages();
        this.displayDate = productDTO.getDisplayDate();
        this.status = updatedStatus;
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    public void addImageUrl(String imageUrl) {
        this.images.add(imageUrl);
    }

    public void updateImageUrls(String imageUrl) {
        this.images = List.of(imageUrl);
    }
}
