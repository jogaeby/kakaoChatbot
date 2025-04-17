package com.chatbot.base.common;

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
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmTalkService {
    private final String TRIAL_RECEIPT_TEMPLATE_ID = "KA01TP250321050424504UONOb04freL";
    private final String TRIAL_BEFORE_TEMPLATE_ID = "KA01TP250321050528900Qt9AwWcle10";
    private final String TRIAL_AFTER_TEMPLATE_ID = "KA01TP250321051103334qqdDFrYQfbE";

    private final String TRIAL_RECEIPT_TEACHER_TEMPLATE_ID = "KA01TP250328050358449CGxVKNf7Fsi";
    private final String TRIAL_BEFORE_TEACHER_TEMPLATE_ID = "KA01TP250325001824569KxxfDfrskOU";
    private final String TRIAL_AFTER_TEACHER_TEMPLATE_ID = "KA01TP250328050450139fRnwuYuBwN7";

    private final String INTERVIEW_RECEIPT_TEMPLATE_ID = "KA01TP250321051403956Og0ws8YQEkI";
    private final String INTERVIEW_BEFORE_TEMPLATE_ID = "KA01TP250321051451962lar8z6U9X9L";
    private final String INTERVIEW_AFTER_TEMPLATE_ID = "KA01TP2503210515594919RLvL8gLazj";

    private final String CHANNEL_ID = "KA01PF240423061604083BUtgyvXfuP9";
    private final String CALLER_1_ID = "010-5736-7927";
    private final String API_KEY = "NCSFZ7LLDWAMUR79";
    private final String API_SECRET_KEY = "ZW1CFHANQLBAUDQONZKODHHFZGNZY0WA";
    /*
        아! 월~금 저녁 11시에는 과제 완료되지 않은 친구들은 과제를 올리라는 메세지와
        11시 59분에는 당일 과제 완료한 친구등 명단이 채팅방에 올라올수 있도록

        토요일 오전에는 월~금 과제가 5회 진행 되진 않은 아이의 명단을 안내하고
        주말동안 올리자고 해주세요!

        일요일은 밤 11시에는 한주간 과제가 완료된 친구들의 명단을 올려서
        성공했다는 메세지를 ㅎㅎ

        가능할까요?????
     */

    public MultipleDetailMessageSentResponse sendTrialReceipt(String phone, String studentName, LocalDateTime date) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(TRIAL_RECEIPT_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{수강생명}", studentName);

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
