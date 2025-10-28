package com.chatbot.base.domain.user.entity;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.user.dto.AccountDto;
import com.chatbot.base.domain.user.dto.AddressDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "account")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = true)
    private String accountName; // 입금자명

    @Column(nullable = true)
    private String bankName; // 은행명

    @Column(nullable = true)
    private String accountNumber; // 계좌번호

    @Column(nullable = false)
    private boolean isDefault; // 기본 환불계좌 여부
    @Builder
    public Account(User user, String accountName, String bankName, String accountNumber, boolean isDefault) {
        this.user = user;
        this.accountName = accountName;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.isDefault = isDefault;
    }

    public static Account create(User user, String accountName, String bankName, String accountNumber, boolean isDefault) {
        return Account.builder()
                .user(user)
                .accountName(accountName)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .isDefault(isDefault)
                .build();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountDto toDto() {
        return AccountDto.builder()
                .accountName(this.accountName)
                .bankName(this.bankName)
                .accountNumber(this.accountNumber)
                .lastModifiedDate(this.getLastModifiedDate())
                .isDefault(this.isDefault)
                .build();
    }

    public void updateAccount(AccountDto accountDto) {
        this.accountName = accountDto.getAccountName();
        this.bankName = accountDto.getBankName();
        this.accountNumber = accountDto.getAccountNumber();
        this.isDefault = accountDto.isDefault();
    }
}
