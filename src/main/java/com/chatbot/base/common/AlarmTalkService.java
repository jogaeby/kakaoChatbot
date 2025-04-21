package com.chatbot.base.common;

import com.chatbot.base.domain.reservation.dto.RoomTourReservationDTO;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmTalkService {
    private final String ROOM_TOUR_ASSIGNMENT_TEMPLATE_ID = "KA01TP250417024022333Svetc3iatS8";


    private final String CHANNEL_ID = "KA01PF250416030320653KQJWjKEaobh";
    private final String CALLER_1_ID = "010-7562-1588";
    private final String API_KEY = "NCSC6ANN3TR6J2MX";
    private final String API_SECRET_KEY = "QUWPDXWG66YWSDLEVFSOPUOCVBZU8DQD";
    /*
        아! 월~금 저녁 11시에는 과제 완료되지 않은 친구들은 과제를 올리라는 메세지와
        11시 59분에는 당일 과제 완료한 친구등 명단이 채팅방에 올라올수 있도록

        토요일 오전에는 월~금 과제가 5회 진행 되진 않은 아이의 명단을 안내하고
        주말동안 올리자고 해주세요!

        일요일은 밤 11시에는 한주간 과제가 완료된 친구들의 명단을 올려서
        성공했다는 메세지를 ㅎㅎ

        가능할까요?????
     */

    public MultipleDetailMessageSentResponse sendRoomTourAssignment(RoomTourReservationDTO roomTourReservationDTO) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(ROOM_TOUR_ASSIGNMENT_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{고객명}", roomTourReservationDTO.getName());
        variables.put("#{룸투어 날짜}", roomTourReservationDTO.getVisitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        kakaoOption.setVariables(variables);

        Message message = new Message();
        message.setFrom(CALLER_1_ID);
        message.setTo(roomTourReservationDTO.getPhone());
        message.setKakaoOptions(kakaoOption);

        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            return messageService.send(message);
        } catch (NurigoMessageNotReceivedException e) {
            log.error("{} {}",e.getFailedMessageList(),e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            throw new RuntimeException(e.getMessage());
        }
    }
}
