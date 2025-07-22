package com.chatbot.base.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SuggestionInfoDto {
    private String id;
    private String name;
    private String phone;
    private List<String> images;
    private String branchName;
    private String brandName;
    private String comment;
    private String status;
    private String createAt;
}
