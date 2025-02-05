package com.chatbot.base.controller.web;

import com.chatbot.base.common.HttpService;
import com.chatbot.base.domain.member.constant.MemberRole;
import com.chatbot.base.domain.member.dto.MemberDTO;
import com.chatbot.base.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("members")
public class MemberController {
    private final MemberService memberService;
    private final HttpService httpService;

    @GetMapping("")
    public String getPage() {
        return "member";
    }

    @GetMapping("list")
    public ResponseEntity getMembers()
    {
        try {
            List<MemberDTO> members = memberService.getMembersByRole(MemberRole.MEMBER);

            return ResponseEntity
                    .ok(members);

        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }
    @PostMapping()
    public ResponseEntity addMember(@RequestBody MemberDTO memberDto)
    {
        try {
            memberService.join(memberDto);

            return ResponseEntity
                    .ok()
                    .build();
        }catch (DataIntegrityViolationException e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(403)
                    .build();
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }

    @PatchMapping()
    public ResponseEntity updateMember(@RequestBody MemberDTO memberDto)
    {
        try {
            memberService.update(memberDto);

            return ResponseEntity
                    .ok()
                    .build();
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteMember(@PathVariable String id)
    {
        try {

            if (httpService.isAdmin()) {
                memberService.delete(id);
                return ResponseEntity
                        .ok()
                        .build();
            }else {
                throw new AuthenticationException("권한이 없습니다. 권한 = " + httpService.getMemberRole());
            }
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }
}
