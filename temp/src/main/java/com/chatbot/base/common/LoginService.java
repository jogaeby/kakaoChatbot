package com.chatbot.base.common;

import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.dto.MemberDTO;
import com.chatbot.base.domain.member.service.MemberService;
import com.chatbot.base.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {
    private final MemberService memberService;
    private final JwtService jwtService;
    public String createToken(LoginRequest loginRequest) {
        String id = loginRequest.getId();
        String rawPassword = loginRequest.getPassword();

        Member member = memberService.getMemberById(id);

        if (PasswordUtil.matches(rawPassword,member.getPassword())) {
            MemberDTO memberDTO = MemberDTO.builder()
                    .id(String.valueOf(member.getId()))
                    .name(member.getName())
                    .phone(member.getPhone())
                    .role(member.getRole())
                    .build();

            return jwtService.createToken(memberDTO);
        }

        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
}
