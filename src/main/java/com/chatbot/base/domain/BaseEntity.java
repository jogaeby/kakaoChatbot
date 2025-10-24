package com.chatbot.base.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "lastModified_date")
    private LocalDateTime lastModifiedDate;

    private boolean isUse;

    @PrePersist
    public void onPrePersist(){
        this.createDate = LocalDateTime.now();
        this.lastModifiedDate = this.createDate;
        this.isUse = true;
    }

    @PreUpdate
    public void onPreUpdate(){
        this.lastModifiedDate = LocalDateTime.now();
    }
}
