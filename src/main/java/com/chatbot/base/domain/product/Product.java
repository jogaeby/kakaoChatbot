package com.chatbot.base.domain.product;

import com.chatbot.base.common.StringUtil;
import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.constant.MemberRole;
import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.product.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String memo;

    private String no;

    private String category;

    private String location;

    private String currentPrice;

    private String price;

    private String minPrice;

    private String expectedPrice;

    private LocalDate saleDate;

    private String managerName;

    private String managerPhone;

    private String description;

    private String link;

    private List<String> images = new ArrayList<>();

    private LocalDate displayDate;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY) // Member와 연관관계 설정
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Builder(access = AccessLevel.PRIVATE)
    public Product(String memo, String no, String category, String location, String price, String currentPrice, String minPrice, LocalDate saleDate, String managerName, String managerPhone, String description, String expectedPrice, String link, List<String> images, ProductStatus status, Member member, LocalDate displayDate) {
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
        this.images = images;
        this.status = status;
        this.member = member;
        this.displayDate = displayDate;
    }




    public static Product of(ProductDTO productDTO,Member member) {
        LocalDate displayDate = productDTO.getDisplayDate();
        ProductStatus status;

        switch (displayDate.compareTo(LocalDate.now())) {
            case 0:
                // displayDate가 오늘과 같으면
                status = ProductStatus.DISPLAY;
                break;
            case 1:
                // displayDate가 오늘보다 미래면
                status = ProductStatus.REGISTRATION;
                break;
            case -1:
                // displayDate가 오늘보다 과거이면
                status = ProductStatus.PRE_DISPLAY;
                break;
            default:
                status = ProductStatus.REGISTRATION;
        }

        return Product.builder()
                .memo(productDTO.getMemo())
                .no(productDTO.getNo())
                .category(productDTO.getCategory())
                .location(productDTO.getLocation())
                .price(productDTO.getPrice())
                .currentPrice(productDTO.getCurrentPrice())
                .minPrice(productDTO.getMinPrice())
                .saleDate(productDTO.getSaleDate())
                .managerName(productDTO.getManagerName())
                .managerPhone(StringUtil.formatPhoneNumber(productDTO.getManagerPhone()))
                .description(productDTO.getDescription())
                .expectedPrice(productDTO.getExpectedPrice())
                .saleDate(productDTO.getSaleDate())
                .link(productDTO.getLink())
                .images(productDTO.getImages())
                .status(status)
                .member(member)
                .displayDate(productDTO.getDisplayDate())
                .build();
    }

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

    public void update(ProductDTO productDTO) {
        ProductStatus productStatus = status;
        if (productDTO.getDisplayDate().equals(LocalDate.now())) {
            productStatus = ProductStatus.DISPLAY;
        }


        this.memo = productDTO.getMemo();
        this.no = productDTO.getNo();
        this.category = productDTO.getCategory();
        this.location = productDTO.getLocation();
        this.price = productDTO.getPrice();
        this.currentPrice = productDTO.getCurrentPrice();
        this.minPrice = productDTO.getMinPrice();
        this.managerName = productDTO.getManagerName();
        this.managerPhone = StringUtil.formatPhoneNumber(productDTO.getManagerPhone());
        this.description = productDTO.getDescription();
        this.expectedPrice = productDTO.getExpectedPrice();
        this.saleDate = productDTO.getSaleDate();
        this.link = productDTO.getLink();
        this.images = productDTO.getImages();
        this.displayDate = productDTO.getDisplayDate();
        this.status = productStatus;
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }
}