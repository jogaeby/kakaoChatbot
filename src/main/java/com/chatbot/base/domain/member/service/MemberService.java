package com.chatbot.base.domain.member.service;

import com.chatbot.base.common.PasswordService;
import com.chatbot.base.constant.MemberRole;
import com.chatbot.base.domain.boardingPoint.BoardingPoint;
import com.chatbot.base.domain.boardingPoint.service.BoardingPointService;
import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.dto.MemberDto;
import com.chatbot.base.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordService passwordService;
    private final BoardingPointService boardingPointService;

    public Member getMemberByAccountId(String accountId) {
        return memberRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다"));
    }

    @Transactional
    public void joinMember(MemberDto memberDto) {
        boolean duplicateMember = isDuplicateMemberByKakaoUserKey(memberDto.getKakaoUserKey());
        if (duplicateMember) {
            throw new IllegalArgumentException(memberDto.getKakaoUserKey()+" 중복되는 근무자입니다.");
        }
        String phone = formatPhoneNumber(memberDto.getPhone());
        BoardingPoint boardingPoint = boardingPointService.findById(memberDto.getBoardingPointId());
        Member member = Member.create(memberDto.getName(),phone, memberDto.getGender(),memberDto.getBirthDate(),memberDto.getAddress(),memberDto.getKakaoUserKey(), MemberRole.MEMBER,boardingPoint,memberDto.getGroupName());
        memberRepository.save(member);
    }

    @Transactional
    public void joinManager(MemberDto memberDto) {
        boolean duplicateMember = isDuplicateMemberByAccountId(memberDto.getAccountId());
        if (duplicateMember) {
            throw new IllegalArgumentException(memberDto.getAccountId()+" 중복되는 관리자 아이디입니다.");
        }
        String phone = formatPhoneNumber(memberDto.getPhone());
        String encodePassword = passwordService.encodePassword(memberDto.getPassword().trim());
        Member member = Member.create(memberDto.getAccountId(),encodePassword,memberDto.getName(),phone,memberDto.getKakaoUserKey(), memberDto.getGender(),memberDto.getBirthDate(),memberDto.getAddress(),memberDto.getEmail(), MemberRole.MANAGER);
        memberRepository.save(member);
    }

    @Transactional
    public void updateMember(MemberDto memberDto) {
        Member member = memberRepository.findById(UUID.fromString(memberDto.getId()))
                .orElseThrow(() -> new RuntimeException(memberDto.getId() + " 해당하는 인원이 없습니다."));
        String phone = formatPhoneNumber(memberDto.getPhone());

        if (Objects.nonNull(memberDto.getPassword()) && !memberDto.getPassword().isEmpty()) {
            String encodePassword = passwordService.encodePassword(memberDto.getPassword().trim());
            member.updatePassword(encodePassword);
        }

        BoardingPoint boardingPoint = boardingPointService.findById(memberDto.getBoardingPointId());
        member.updateBoardingPoint(boardingPoint);
        member.updatePhone(phone);
        member.updateMember(memberDto);
    }

    public List<MemberDto> getMembersByRole(MemberRole role) {
        List<Member> members = memberRepository.findAllByRole(role);
        if (role.equals(MemberRole.MANAGER)) {
            return members.stream()
                    .map(Member::toDto)
                    .toList();
        }
        return members.stream()
                .map(Member::toDto)
                .toList();
    }

    public MemberDto getMemberDtoByKakaoUserKey(String kakaoUserKey) {
        return memberRepository.findByKakaoUserKey(kakaoUserKey)
                .orElseThrow(() -> new NoSuchElementException("등록되지 않는 근무자입니다."))
                .toDto();

    }

    public Member getMemberByKakaoUserKey(String kakaoUserKey) {
        return memberRepository.findByKakaoUserKey(kakaoUserKey)
                .orElseThrow(() -> new NoSuchElementException("등록되지 않는 근무자입니다."));
    }

    @Transactional
    public void deleteMember(String id, MemberRole role) throws AuthenticationException {
        Member member = memberRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException(id + " 근무자를 찾을 수 없습니다."));

        if (role.equals(MemberRole.ADMIN)) {
            memberRepository.delete(member);
        }

        if (role.equals(MemberRole.MANAGER)) {
            if (member.getRole().equals(MemberRole.MEMBER)) {
                memberRepository.delete(member);
            }else {
                throw new AuthenticationException(role.getName() + "는 삭제 권한이 없습니다.");
            }

        }

    }

    public String formatPhoneNumber(String phoneNumber) {
        // 모든 특수 문자를 제거하고 숫자만 남김
        return phoneNumber.replaceAll("[^0-9]", "");
    }

    public boolean isDuplicateMemberByKakaoUserKey(String kakaoUserKey) {
        return memberRepository.findByKakaoUserKey(kakaoUserKey).isPresent();
    }

    public boolean isDuplicateMemberByAccountId(String accountId) {
        return memberRepository.findByAccountId(accountId).isPresent();
    }
}
