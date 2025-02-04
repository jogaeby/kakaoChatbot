package com.chatbot.base.common;

import com.chatbot.base.constant.MemberRole;
import com.chatbot.base.dto.MemberInfoDtoFromJwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    public String createMemberInfoToken(MemberInfoDtoFromJwt memberInfoDtoFromJwt) {
        LocalDateTime current = LocalDateTime.now();
        Timestamp isu = Timestamp.valueOf(current);
        Timestamp ex = Timestamp.valueOf(current.plusDays(60));

        Claims claims = Jwts.claims()
                .add("id",memberInfoDtoFromJwt.getId())
                .add("accountId",memberInfoDtoFromJwt.getAccountId())
                .add("name",memberInfoDtoFromJwt.getName())
                .add("phone",memberInfoDtoFromJwt.getPhone())
                .add("email",memberInfoDtoFromJwt.getEmail())
                .add("role",memberInfoDtoFromJwt.getRole().getName())
                .build();

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claims(claims)
                .issuedAt(isu)
                .expiration(ex)
                .subject("userInfo")
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact()
                ;
    }

    public MemberInfoDtoFromJwt getMemberInfoFromToken(String token) {
        Claims body = Jwts.parser().setSigningKey(JWT_SECRET).build().parseClaimsJws(token).getBody();
        String id = String.valueOf(body.get("id"));
        String academyId = String.valueOf(body.get("academyId"));
        String accountId = String.valueOf(body.get("accountId"));
        String name = String.valueOf(body.get("name"));
        String phone = String.valueOf(body.get("phone"));
        String email = String.valueOf(body.get("email"));
        String role = String.valueOf(body.get("role"));
        MemberRole memberRole = MemberRole.fromString(role);

        MemberInfoDtoFromJwt memberInfoDtoFromJwt = MemberInfoDtoFromJwt.builder()
                .id(id)
                .accountId(accountId)
                .name(name)
                .phone(phone)
                .email(email)
                .role(memberRole)
                .build();

        return memberInfoDtoFromJwt;
    }

    public boolean isValidateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).build().parse(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {

            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {

            log.warn("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {

            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {

            log.warn("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
