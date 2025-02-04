package com.chatbot.base.controller.web;

import com.chatbot.base.common.HttpService;
import com.chatbot.base.constant.MemberRole;
import com.chatbot.base.domain.member.dto.MemberDto;
import com.chatbot.base.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping()
    public ResponseEntity addMember(@RequestBody MemberDto memberDto)
    {
        try {
            memberService.joinMember(memberDto);

            return ResponseEntity
                    .ok()
                    .build();
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
    @PatchMapping()
    public ResponseEntity updateMember(@RequestBody MemberDto memberDto)
    {
        try {
            memberService.updateMember(memberDto);

            return ResponseEntity
                    .ok()
                    .build();
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @GetMapping("list")
    public ResponseEntity getMembers()
    {
        try {
            List<MemberDto> members = memberService.getMembersByRole(MemberRole.MEMBER);

            return ResponseEntity
                    .ok(members);

        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteMember(@PathVariable String id)
    {
        try {
            MemberRole memberRole = httpService.getMemberRole();

            memberService.deleteMember(id, memberRole);
            return ResponseEntity
                    .ok()
                    .build();

        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}
