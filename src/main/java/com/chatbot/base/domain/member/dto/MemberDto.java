package com.chatbot.base.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberDto {
    private String id;
    private String accountId;
    private String password;
    private String groupName;
    private String name;
    private String phone;
    private String gender;
    private String birthDate;
    private String address;
    private String kakaoUserKey;
    private String email;
    private String role;
    private String boardingPointId;
    private String boardingPointName;
    private LocalDateTime createDate;
}
