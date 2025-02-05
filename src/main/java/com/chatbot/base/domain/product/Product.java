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
    private String description;
    private String link;
    private List<String> images = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    @ManyToOne(fetch = FetchType.LAZY) // Member와 연관관계 설정
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Builder(access = AccessLevel.PRIVATE)
    public Product(String title, String description, String link, List<String> images, ProductStatus status, Member member) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.images = images;
        this.status = status;
        this.member = member;
    }

    public static Product of(ProductDTO productDTO,Member member) {
        return Product.builder()
                .title(productDTO.getTitle())
                .description(productDTO.getDescription())
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
                .description(description)
                .link(link)
                .images(images)
                .status(status.getName())
                .memberId(member.getId())
                .createDate(getCreateDate().toLocalDate())
                .build();
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }
}