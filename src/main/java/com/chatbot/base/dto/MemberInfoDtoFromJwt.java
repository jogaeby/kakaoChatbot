package com.chatbot.base.dto;

import com.chatbot.base.constant.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoDtoFromJwt {
    private String id;
    private String accountId;
    private String name;
    private String phone;
    private String email;
    private MemberRole role;
}
