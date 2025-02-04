package com.chatbot.base.domain.member;

import com.chatbot.base.constant.MemberRole;
import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.boardingPoint.BoardingPoint;
import com.chatbot.base.domain.member.dto.MemberDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Getter
@Entity
@Table(name = "member")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    private String accountId;
    private String password;
    private String name;
    private String phone;
    private String gender;
    private String birthDate;
    private String address;
    private String email;
    private String kakaoUserKey;
    private String groupName;
    @Enumerated(EnumType.STRING)
    private MemberRole role;
    private boolean isAlarmTalk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boarding_point_id")  // 외래 키 컬럼명 설정
    private BoardingPoint boardingPoint;

    @Builder
    public Member(String accountId, String password, String name, String phone, String gender,String birthDate,String address,String email, String kakaoUserKey, MemberRole role, BoardingPoint boardingPoint, String groupName) {
        this.accountId = accountId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.birthDate = birthDate;
        this.address = address;
        this.email = email;
        this.role = role;
        this.kakaoUserKey = kakaoUserKey;
        this.groupName = groupName;
        this.isAlarmTalk = true;
        this.boardingPoint = boardingPoint;
    }

    public static Member create(String accountId, String password, String name, String phone, String kakaoUserKey,String gender,String birthDate,String address, String email, MemberRole role) {
        return Member.builder()
                .accountId(accountId)
                .password(password)
                .kakaoUserKey(kakaoUserKey)
                .name(name)
                .phone(phone)
                .email(email)
                .gender(gender)
                .birthDate(birthDate)
                .address(address)
                .role(role)
                .build();
    }

    public static Member create(String name, String phone, String gender, String birthDate, String address,String kakaoUserKey,MemberRole role, BoardingPoint boardingPoint, String groupName) {
        return Member.builder()
                .kakaoUserKey(kakaoUserKey)
                .groupName(groupName)
                .name(name)
                .phone(phone)
                .gender(gender)
                .birthDate(birthDate)
                .address(address)
                .role(role)
                .boardingPoint(boardingPoint)
                .build();
    }

    public MemberDto toDto() {
        String boardingPointName = "";
        String boardingPointId = "";

        if (Objects.nonNull(boardingPoint)) {
            boardingPointName = boardingPoint.getBusName() + " - " + boardingPoint.getBoardPoint();
            boardingPointId = String.valueOf(boardingPoint.getId());
        }

        return MemberDto.builder()
                .id(getId().toString())
                .accountId(accountId)
                .name(name)
                .phone(phone)
                .gender(gender)
                .birthDate(birthDate)
                .address(address)
                .kakaoUserKey(kakaoUserKey)
                .email(email)
                .createDate(getCreateDate())
                .role(role.getName())
                .boardingPointId(boardingPointId)
                .boardingPointName(boardingPointName)
                .groupName(groupName)
                .build();
    }


    public void updateAlarmTalk(boolean isAlarmTalk) {
        this.isAlarmTalk = isAlarmTalk;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateBoardingPoint(BoardingPoint boardingPoint) {
        this.boardingPoint = boardingPoint;
    }

    public void updateMember(MemberDto memberDto) {
        this.accountId = memberDto.getAccountId();
        this.name = memberDto.getName();
        this.email = memberDto.getEmail();
        this.birthDate = memberDto.getBirthDate();
        this.address = memberDto.getAddress();
        this.kakaoUserKey = memberDto.getKakaoUserKey();
        this.gender = memberDto.getGender();
        this.groupName = memberDto.getGroupName();
    }
}