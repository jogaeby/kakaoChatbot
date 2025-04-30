package com.chatbot.base.common;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmTalkService {
    private final String ROOM_TOUR_ASSIGNMENT_TEMPLATE_ID = "KA01TP250429060751487hy6liQXD2Ju";


    private final String CHANNEL_ID = "KA01PF250429022937123up5LXRNxBuq";
    private final String CALLER_1_ID = "010-3919-0126";
    private final String API_KEY = "NCSNWVYDNNO5CTVN";
    private final String API_SECRET_KEY = "TNHOIYTJ6XD83P7UWI8V0DPACEXMDUJN";


    public MultipleDetailMessageSentResponse sendReservation(String phone, Reservation reservation) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(ROOM_TOUR_ASSIGNMENT_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{접수번호}", String.valueOf(reservation.getId()));
        variables.put("#{이름}", reservation.getName());
        variables.put("#{연락처}", reservation.getPhone());
        variables.put("#{예약일}", reservation.getReservationDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 H시 m분")));
        variables.put("#{희망완료알}", reservation.getHopeCompleteDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 H시 m분")));
        variables.put("#{희망요금}", reservation.getHopePrice());
        variables.put("#{출발지}", reservation.getDepart());
        variables.put("#{도착지}", reservation.getArrive());
        variables.put("#{의뢰내용}", reservation.getMessage());
        variables.put("#{추가요청사항}", reservation.getComment());

        kakaoOption.setVariables(variables);

        Message message = new Message();
        message.setFrom(CALLER_1_ID);
        message.setTo(phone);
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
