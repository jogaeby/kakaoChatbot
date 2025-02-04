package com.chatbot.base.controller.web;

import com.chatbot.base.common.HttpService;
import com.chatbot.base.constant.MemberRole;
import com.chatbot.base.domain.boardingPoint.dto.BoardingPointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.chatbot.base.domain.boardingPoint.service.BoardingPointService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("boardingPoint")
public class BoardingPointController {
    private final BoardingPointService boardingPointService;
    private final HttpService httpService;

    @GetMapping("")
    public String getPage() {
        return "boardingPoint";
    }

    @PostMapping()
    public ResponseEntity addBoardingPoint(@RequestBody BoardingPointDto boardingPointDto)
    {
        try {
            log.info("{} {}",boardingPointDto.getDepartBusTime(),boardingPointDto.getStartWorkTime());
            boardingPointService.saveBoardingPoint(boardingPointDto);

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
    public ResponseEntity updateMember(@RequestBody BoardingPointDto boardingPointDto)
    {
        try {
            boardingPointService.updateBoardingPoint(boardingPointDto);

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
            List<BoardingPointDto> boardingPointDtos = boardingPointService.findAll();


            return ResponseEntity
                    .ok(boardingPointDtos);

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

            boardingPointService.deleteById(id);
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
