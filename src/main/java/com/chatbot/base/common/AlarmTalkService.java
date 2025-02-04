package com.chatbot.base.common;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.domain.work.dto.WorkDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmTalkService {
    private final String HOST_URL = "52.78.230.246:8080";
    private final String RESERVATION_CONFIRM_TEMPLATE_ID = "KA01TP24120407202339726MS6e5CSHw";
    private final String RESERVATION_BOARD_BUS_TEMPLATE_ID = "KA01TP241204083247051H7ZjDGSXniX";
    private final String RESERVATION_CANCEL_ADMIN_TEMPLATE_ID = "KA01TP241204065728595QofjhMrupq7";
    private final String RESERVATION_CANCEL_MEMBER_TEMPLATE_ID = "KA01TP241129024401692kA6RcuW2bVk";
    private final String CHANNEL_ID = "KA01PF241017074517426GY6N97Y5Vci";
    private final String CALLER_1_ID = "010-9922-9545";
    private final String CALLER_2_ID = "010-9988-5400";

    private final String API_KEY = "NCS2OAH9ML25YONL";
    private final String API_SECRET_KEY = "JUWTKY6FZDDSY8PIHVECUTF4ERN7VGXC";

    public void sendReservationPermission(ReservationDto reservation, WorkDto workDtoByDate) {
        String date = reservation.getReservationDate();
        String koreanDayOfWeek = StringFormatterService.getKoreanDayOfWeek(date);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String workTime = workDtoByDate.getTime().format(timeFormatter);

        String name = reservation.getName();
        String phoneNumber = reservation.getPhone();
        String busName = reservation.getBusName();
        String busZone = reservation.getBoardPoint();

        LocalTime timeDifference = reservation.getTimeDifference();
        String busTime = workDtoByDate.getDateTime().minusHours(timeDifference.getHour()).minusMinutes(timeDifference.getMinute()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");
        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(RESERVATION_CONFIRM_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{이름}", name);
        variables.put("#{날짜}", date);
        variables.put("#{요일}", koreanDayOfWeek);
        variables.put("#{작업시작 시간}", workTime);
        variables.put("#{통근버스}", busName);
        variables.put("#{탑승지}", busZone);
        variables.put("#{통근버스 출발시간}", busTime);
        variables.put("#{관리자연락처}", CALLER_2_ID);
        variables.put("#{urlConfrim}", HOST_URL+"/reservation/ready?id="+reservation.getId()+"&status="+ReservationStatus.ADMISSION.getName());
        variables.put("#{urlCancle}",  HOST_URL+"/reservation/ready?id="+reservation.getId()+"&status="+ReservationStatus.CANCEL.getName());
        kakaoOption.setVariables(variables);

        /*
            #{이름} 님
            근무일: #{날짜} #{요일}
            작업 시작 시간: #{작업시작 시간}

            출근 준비 할 시간이 되신거 같습니다.

            🚌통근버스
            버스: #{통근버스}
            탑승지: #{탑승지}
            버스 출발시간: #{통근버스 출발시간}

            통근버스 출발시간 20분 전에
            탑승지에 도착해 바로 탑승 해주세요


            ★출근 준비 한다면
            "출근 준비 중" 누르고 출근하세요

            ★부득이 출근을 취소해야 한다면
            "출근 취소" 눌러주세요

            문의사항: #{관리자연락처}
        */


        Message message = new Message();
        message.setFrom(CALLER_1_ID);
        message.setTo(phoneNumber);
        message.setKakaoOptions(kakaoOption);


        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            MultipleDetailMessageSentResponse send = messageService.send(message);
        } catch (NurigoMessageNotReceivedException e) {
            log.error("{} {}",e.getFailedMessageList(),e.getMessage());
        } catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
        }
    }

    @Async
    public void sendAllFirstAlarm(List<ReservationDto> reservationsByDate, WorkDto workDtoByDate) {

        List<ReservationDto> filterReservation = reservationsByDate.stream()
                .filter(reservation ->
                        reservation.getStatus().equals(ReservationStatus.APPLY.getName()) || reservation.getStatus().equals(ReservationStatus.ADMISSION.getName()))
                .collect(Collectors.toList());

        filterReservation.forEach(reservationDto -> {
            sendReservationPermission(reservationDto,workDtoByDate);
        });
    }

    @Async
    public void sendCancelAlarmByAdmin(ReservationDto reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String name = reservation.getName();
        String date = reservation.getReservationDate();
        String koreanDayOfWeek = StringFormatterService.getKoreanDayOfWeek(date);

        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(RESERVATION_CANCEL_ADMIN_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{이름}", name);
        variables.put("#{날짜}", date);
        variables.put("#{요일}", koreanDayOfWeek);
        variables.put("#{관리자연락처}", CALLER_2_ID);
        kakaoOption.setVariables(variables);

        /*
            #{이름} 님
            근무날짜 #{날짜} #{요일}
            부득이하게  출근 취소 되었습니다.

            문의사항: #{관리자연락처}
            현장사정에 따라
            부득이하게 출근이 취소 되었습니다
            미안합니다

            다음 출근은 우선적으로
            투입 될 수 있도록 신경 쓰겠습니다.
            (출근 일정을 미리 확인하고, 출근 신청을 미리 해주세요)
         */

        Message message = new Message();
        message.setFrom(CALLER_1_ID);
        message.setTo(reservation.getPhone());
        message.setKakaoOptions(kakaoOption);


        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            MultipleDetailMessageSentResponse send = messageService.send(message);
        } catch (NurigoMessageNotReceivedException e) {
            log.error("{} {}",e.getFailedMessageList(),e.getMessage());
        } catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
        }
    }

    @Async
    public void sendCancelAlarmByMember(ReservationDto reservation) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String name = reservation.getName();
        String date = reservation.getReservationDate();
        String koreanDayOfWeek = StringFormatterService.getKoreanDayOfWeek(date);

        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(RESERVATION_CANCEL_MEMBER_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{이름}",name);
        variables.put("#{날짜}", date);
        variables.put("#{요일}", koreanDayOfWeek);
        variables.put("#{관리자연락처}", CALLER_2_ID);
        kakaoOption.setVariables(variables);

        /*
            #{이름} 님
            근무날짜 #{날짜} #{요일}
            출근 취소 되었습니다.

            문의사항: #{관리자연락처}
            출근 일정을 확인 하고
            출근 신청을  미리 해두세요
         */

        Message message = new Message();
        message.setFrom(CALLER_1_ID);
        message.setTo(reservation.getPhone());
        message.setKakaoOptions(kakaoOption);


        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            MultipleDetailMessageSentResponse send = messageService.send(message);
        } catch (NurigoMessageNotReceivedException e) {
            log.error("{} {}",e.getFailedMessageList(),e.getMessage());
        } catch (Exception e) {
            log.error("{} {}",e.getMessage(),e);
        }
    }

    @Async
    public void sendBoardBusAlarm(ReservationDto reservation, WorkDto workDtoByDate) {

        String name = reservation.getName();
        String date = reservation.getReservationDate();
        String koreanDayOfWeek = StringFormatterService.getKoreanDayOfWeek(date);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String workTime = workDtoByDate.getTime().format(timeFormatter);
        String busName = reservation.getBusName();
        String busZone = reservation.getBoardPoint();

        LocalTime timeDifference = reservation.getTimeDifference();
        LocalDateTime busTime = workDtoByDate.getDateTime().minusHours(timeDifference.getHour()).minusMinutes(timeDifference.getMinute());

        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(RESERVATION_BOARD_BUS_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{이름}", name);
        variables.put("#{날짜}", date);
        variables.put("#{요일}", koreanDayOfWeek);
        variables.put("#{작업시작 시간}", workTime);
        variables.put("#{통근버스}", busName);
        variables.put("#{탑승지}", busZone);
        variables.put("#{통근버스 출발시간}", busTime.toString());
        variables.put("#{관리자 연락처}", CALLER_2_ID);
        variables.put("#{urlConfrim}",  HOST_URL+"/reservation/boarding?id="+reservation.getId()+"&status="+ReservationStatus.ADMISSION.getName());
        variables.put("#{urlCancle}",  HOST_URL+"/reservation/boarding?id="+reservation.getId()+"&status="+ReservationStatus.CANCEL.getName());
        kakaoOption.setVariables(variables);

        /*
            #{이름} 님
            근무일: #{날짜} #{요일}
            작업 시작 시간: #{작업시작 시간}

            🚌통근버스
            버스: #{통근버스}
            탑승지: #{탑승지}
            버스 출발시간: #{통근버스 출발시간}

            통근버스 탑승 하셨다면
            "탑승" 버튼을 눌러주세요

            탑승을 못했다면
            "통근버스 못탐" 버튼을 눌러주세요

            문의사항: #{관리자 연락처}
         */

        Message message = new Message();
        message.setFrom(CALLER_1_ID);
        message.setTo(reservation.getPhone());
        message.setKakaoOptions(kakaoOption);


        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            MultipleDetailMessageSentResponse send = messageService.send(message);
        } catch (NurigoMessageNotReceivedException e) {
            log.error("{} {}",e.getFailedMessageList(),e.getMessage());
        } catch (Exception e) {
            log.error("{} {}",e.getMessage(),e);
        }
    }

}
