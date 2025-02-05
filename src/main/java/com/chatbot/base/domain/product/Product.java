package com.chatbot.base.domain.product;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.constant.MemberRole;
import com.chatbot.base.domain.product.constant.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
}