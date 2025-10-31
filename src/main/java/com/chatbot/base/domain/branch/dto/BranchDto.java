package com.chatbot.base.domain.branch.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BranchDto {
    private String id;
    private String name;
    private String ownerName;
    private String ownerPhone;

    private String accountName;

    private String accountNum;
    private String accountBankName;
}
