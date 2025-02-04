package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.member.dto.MemberDto;
import com.chatbot.base.domain.member.service.MemberService;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.domain.reservation.service.ReservationService;
import com.chatbot.base.domain.work.Work;
import com.chatbot.base.domain.work.dto.WorkDto;
import com.chatbot.base.domain.work.service.WorkService;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Context;
import com.chatbot.base.view.KakaoChatBotView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/reservation")
public class KakaoReservationController {
    private final WorkService workService;
    private ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();
    private final ReservationService reservationService;
    private final KakaoChatBotView kakaoChatBotView;
    private final MemberService memberService;
    private final AlarmTalkService alarmService;

    @PostMapping(value = "test")
    public void test(@RequestBody String chatBotRequest) {
        log.info("{}",chatBotRequest);
    }

    @PostMapping(value = "auth")
    public ChatBotResponse memberAuth(@RequestBody ChatBotRequest chatBotRequest) {
        String userKey = chatBotRequest.getUserKey();
        try {

            memberService.getMemberDtoByKakaoUserKey(userKey);

            return kakaoChatBotView.memberConfirmView();
        }catch (NoSuchElementException e) {
            return kakaoChatBotView.isNotMember(userKey);
        }catch (Exception e) {
            log.error("memberAuth: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "confirm")
    public ChatBotResponse reservationConfirm(@RequestBody ChatBotRequest chatBotRequest) {
        String userKey = chatBotRequest.getUserKey();
        try {
            LocalDate reservationDate = chatBotRequest.getReservationDate();

            MemberDto memberByKakaoUserKey = memberService.getMemberDtoByKakaoUserKey(userKey);

            return kakaoChatBotView.reservationConfirm(memberByKakaoUserKey.getName(), memberByKakaoUserKey.getPhone(), reservationDate);

        }catch (NoSuchElementException e) {
            return kakaoChatBotView.isNotMember(userKey);
        }catch (Exception e) {
            log.error("reservationConfirm: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "execute")
    @Transactional
    public ChatBotResponse reservationExecute(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            Context reservationInfoContext = chatBotRequest.getReservationInfoContext();
            String userKey = chatBotRequest.getUserKey();
            String reservationDate = String.valueOf(reservationInfoContext.getParams().get("reservationDate"));

            MemberDto memberByKakaoUserKey = memberService.getMemberDtoByKakaoUserKey(userKey);
            String name = memberByKakaoUserKey.getName();
            String phone = memberByKakaoUserKey.getPhone();

            log.info("{} {} {} {}",userKey,name, phone, reservationDate);

            ReservationDto reservationDto = reservationService.saveReservation(userKey, name, phone, reservationDate).toDto();

            Optional<Work> workByDate = workService.getWorkByDate(LocalDate.parse(reservationDto.getReservationDate()));

            if (workByDate.isPresent()) {
                WorkDto workDtoByDate = workByDate.get().toDto();
                alarmService.sendReservationPermission(reservationDto,workDtoByDate);
            }

            return kakaoChatBotView.reservationSuccess(reservationDto);
        }catch (DuplicateKeyException e) {
            log.error("reservationExecute: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException(e.getMessage());
        }catch (Exception e) {
            log.error("reservationExecute: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "list")
    public ChatBotResponse getReservationList(@RequestBody ChatBotRequest chatBotRequest) {
        String userKey = chatBotRequest.getUserKey();
        try {
            MemberDto memberByKakaoUserKey = memberService.getMemberDtoByKakaoUserKey(userKey);

            List<Reservation> reservations = reservationService.getAllReservationByKakaoUserkey(userKey);

            return kakaoChatBotView.getReservationList(reservations);
        }catch (NoSuchElementException e) {
            return kakaoChatBotView.isNotMember(userKey);
        }catch (DuplicateKeyException e) {
            log.error("getReservationList: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException(e.getMessage());
        }catch (Exception e) {
            log.error("getReservationList: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "detail")
    public ChatBotResponse getReservationDetail(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            String reservationId = chatBotRequest.getChoiceParam();
            Reservation reservation = reservationService.getReservation(reservationId);

            return kakaoChatBotView.reservationDetail(reservation);
        }catch (NoSuchElementException e) {
            log.error("getReservationDetail: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException("존재하지 않는 근무신청 내역입니다.");
        }catch (DuplicateKeyException e) {
            log.error("getReservationDetail: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException(e.getMessage());
        }catch (Exception e) {
            log.error("getReservationDetail: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "cancel")
    public ChatBotResponse cancelReservation(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            String userKey = chatBotRequest.getUserKey();

            memberService.getMemberDtoByKakaoUserKey(userKey);

            String reservationId = chatBotRequest.getChoiceParam();
            reservationService.cancelReservationByMember(reservationId);

            return kakaoChatBotView.reservationCancel();
        }catch (NoSuchElementException e) {
            log.error("cancelReservation: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException("존재하지 않는 근무신청 내역입니다.");
        }catch (DuplicateKeyException e) {
            log.error("cancelReservation: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException(e.getMessage());
        }catch (Exception e) {
            log.error("cancelReservation: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "memberKey")
    public ChatBotResponse getMemberKey(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            String userKey = chatBotRequest.getUserKey();

            return kakaoChatBotView.kakaoUserKeyView(userKey);
        }catch (Exception e) {
            log.error("getReservationDetail: {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException();
        }
    }



}
