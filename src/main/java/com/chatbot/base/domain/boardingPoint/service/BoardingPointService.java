package com.chatbot.base.domain.boardingPoint.service;

import com.chatbot.base.domain.boardingPoint.BoardingPoint;
import com.chatbot.base.domain.boardingPoint.dto.BoardingPointDto;
import com.chatbot.base.domain.boardingPoint.repository.BoardingPointRepository;
import com.chatbot.base.domain.member.repository.MemberRepository;
import com.chatbot.base.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardingPointService {
    private final BoardingPointRepository boardingPointRepository;
    private final MemberRepository memberRepository;
    @Transactional
    public void saveBoardingPoint(BoardingPointDto boardingPointDto) {
        BoardingPoint boardingPoint = BoardingPoint.create(boardingPointDto.getBusName(), boardingPointDto.getBusNumber(), boardingPointDto.getDriverName(), boardingPointDto.getDriverPhone(), boardingPointDto.getBoardPoint(), boardingPointDto.getStartWorkTime(), boardingPointDto.getDepartBusTime());


        boardingPointRepository.save(boardingPoint);
    }

    public List<BoardingPointDto> findAll() {

        return boardingPointRepository.findAll().stream()
                .map(BoardingPoint::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteById(String id) {
        BoardingPoint boardingPoint = findById(id);
        memberRepository.setBoardingPointToNull(boardingPoint);

        boardingPointRepository.delete(boardingPoint);
    }
    @Transactional
    public void updateBoardingPoint(BoardingPointDto boardingPointDto) {
        String id = boardingPointDto.getId();
        BoardingPoint boardingPoint = findById(id);

        boardingPoint.update(boardingPointDto);
    }

    public BoardingPoint findById(String id) {
        return boardingPointRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException(id + " 해당하는 탑승지가 없습니다."));
    }
}
