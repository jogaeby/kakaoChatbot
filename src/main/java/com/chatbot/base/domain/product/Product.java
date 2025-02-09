package com.chatbot.base.domain.product;

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
    private String title;

    private String no;

    private String category;

    private String location;

    private String price;

    private String minPrice;

    private String expectedPrice;

    private LocalDate saleDate;

    private String managerName;

    private String managerPhone;

    private String link;

    private List<String> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY) // Member와 연관관계 설정
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Builder(access = AccessLevel.PRIVATE)
    public Product(String title, String no, String category, String location, String price, String minPrice, LocalDate saleDate, String managerName, String managerPhone, String expectedPrice, String link, List<String> images, ProductStatus status, Member member) {
        this.title = title;
        this.no = no;
        this.category = category;
        this.location = location;
        this.price = price;
        this.minPrice = minPrice;
        this.saleDate = saleDate;
        this.managerName = managerName;
        this.managerPhone = managerPhone;
        this.expectedPrice = expectedPrice;
        this.link = link;
        this.images = images;
        this.status = status;
        this.member = member;
    }




    public static Product of(ProductDTO productDTO,Member member) {
        return Product.builder()
                .title(productDTO.getTitle())
                .no(productDTO.getNo())
                .category(productDTO.getCategory())
                .location(productDTO.getLocation())
                .price(productDTO.getPrice())
                .minPrice(productDTO.getMinPrice())
                .saleDate(productDTO.getSaleDate())
                .managerName(productDTO.getManagerName())
                .managerPhone(productDTO.getManagerPhone())
                .expectedPrice(productDTO.getExpectedPrice())
                .saleDate(productDTO.getSaleDate())
                .link(productDTO.getLink())
                .images(productDTO.getImages())
                .status(ProductStatus.REGISTRATION)
                .member(member)
                .build();
    }

    public ProductDTO toDTO() {
        return ProductDTO.builder()
                .id(getUuid().toString())
                .title(title)
                .no(no)
                .category(category)
                .location(location)
                .price(price)
                .minPrice(minPrice)
                .expectedPrice(expectedPrice)
                .saleDate(saleDate)
                .managerName(managerName)
                .managerPhone(managerPhone)
                .link(link)
                .images(images)
                .status(status.getName())
                .statusPriority(status.getPriority())
                .memberId(member.getId())
                .createDate(getCreateDate().toLocalDate())
                .build();
    }

    public void update(ProductDTO productDTO) {
        this.title = productDTO.getTitle();
        this.no = productDTO.getNo();
        this.category = productDTO.getCategory();
        this.location = productDTO.getLocation();
        this.price = productDTO.getPrice();
        this.minPrice = productDTO.getMinPrice();
        this.saleDate = productDTO.getSaleDate();
        this.managerName = productDTO.getManagerName();
        this.managerPhone = productDTO.getManagerPhone();
        this.expectedPrice = productDTO.getExpectedPrice();
        this.saleDate = productDTO.getSaleDate();
        this.link = productDTO.getLink();
        this.images = productDTO.getImages();
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }
}