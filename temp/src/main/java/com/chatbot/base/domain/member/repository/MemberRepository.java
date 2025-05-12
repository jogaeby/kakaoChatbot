package com.chatbot.base.domain.member.repository;

import com.chatbot.base.domain.member.constant.MemberRole;
import com.chatbot.base.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findById(String id);
    List<Member> findAllByRole(MemberRole role);
    void deleteById(String id);
}
