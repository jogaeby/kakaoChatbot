package com.chatbot.base.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BranchDto {
    private String brandName;
    private String branchName;
    private String name;
    private String phone;
    private String managerName;
    private String managerPhone;
}
