package com.chatbot.base.common;

import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.service.MemberService;
import com.chatbot.base.dto.LoginRequest;
import com.chatbot.base.dto.MemberInfoDtoFromJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {
    private final MemberService memberService;
    private final PasswordService passwordService;
    private final JwtService jwtService;


    public String createToken(LoginRequest loginRequest) {
        String accountId = loginRequest.getId();
        String rawPassword = loginRequest.getPassword();

        Member member = memberService.getMemberByAccountId(accountId);


//        Student student = memberService.getStudent(accountId, );
        if (passwordService.matches(rawPassword,member.getPassword())) {
            MemberInfoDtoFromJwt memberInfoDtoFromJwt = MemberInfoDtoFromJwt.builder()
                    .id(String.valueOf(member.getId()))
                    .accountId(member.getAccountId())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .email(member.getEmail())
                    .role(member.getRole())
                    .build();

            return jwtService.createMemberInfoToken(memberInfoDtoFromJwt);
        }


        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

}
