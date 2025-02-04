package com.chatbot.base.domain.boardingPoint.repository;

import com.chatbot.base.constant.MemberRole;
import com.chatbot.base.domain.boardingPoint.BoardingPoint;
import com.chatbot.base.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardingPointRepository extends JpaRepository<BoardingPoint, UUID> {
}
