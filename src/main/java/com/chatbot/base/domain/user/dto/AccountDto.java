package com.chatbot.base.domain.user.dto;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Getter
@Builder
public class AccountDto {
    private String accountName; // 입금자명
    private String bankName; // 은행명
    private String accountNumber; // 계좌번호
    private boolean isDefault;

}
