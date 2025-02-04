package com.chatbot.base.domain.member.repository;

import com.chatbot.base.constant.MemberRole;
import com.chatbot.base.domain.boardingPoint.BoardingPoint;
import com.chatbot.base.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByAccountId(String accountId);

    List<Member> findAllByRole(MemberRole role);

    Optional<Member> findByKakaoUserKey(String kakaoUserKey);

    @Modifying
    @Query("UPDATE Member m SET m.boardingPoint = null WHERE m.boardingPoint = :boardingPoint")
    void setBoardingPointToNull(BoardingPoint boardingPoint);
}
