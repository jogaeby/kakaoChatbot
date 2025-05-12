package com.chatbot.base.domain.member.service;

import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.constant.MemberRole;
import com.chatbot.base.domain.member.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    Member getMemberById(String id);

    List<MemberDTO> getMembersByRole(MemberRole role);

    MemberDTO join(MemberDTO memberDTO);

    MemberDTO update(MemberDTO memberDTO);

    void delete(String id);
}
