package com.chatbot.base.common;

import com.chatbot.base.common.util.StringFormatterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmTalkService {
    private final String AS_RECEIPT_TEMPLATE_ID = "KA01TP250617121512857LzamFUV5dx5";
    private final String AS_ASSIGNMENT_TEMPLATE_ID = "KA01TP250617122710836P7sQ7pe9L3E";
    private final String AS_COMPLETE_TEMPLATE_ID = "KA01TP250617121620721QZsI9IUR7q5";


    private final String CALLER_1_ID = "010-7102-7421";
    private final String CHANNEL_ID = "KA01PF250613092804345VGzjHfhV1Uo";
    private final String API_KEY = "NCSXKGY4BH4NF1LS";
    private final String API_SECRET_KEY = "1NG2OLUMSXYU2UWRUXKNOE6RECQ8NUG1";


    public MultipleDetailMessageSentResponse sendASReceipt(String targetPhone, String receiptId, String name, String phone, String address, String symptoms, String url) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(AS_RECEIPT_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{접수번호}", receiptId);
        variables.put("#{이름}",name);
        variables.put("#{연락처}", phone);
        variables.put("#{주소}", address);
        variables.put("#{증상}", symptoms);
        variables.put("#{url}", url);

        kakaoOption.setVariables(variables);

        Message message = new Message();
        message.setFrom(CALLER_1_ID);
        message.setTo(StringFormatterUtil.cleanPhoneNumber(targetPhone));
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

    public MultipleDetailMessageSentResponse sendASAssignment(String targetPhone, String receiptId, String name, String phone, String address, String symptoms, String engineerName, String engineerPhone,String url) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(AS_ASSIGNMENT_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{접수번호}", receiptId);
        variables.put("#{이름}",name);
        variables.put("#{연락처}", phone);
        variables.put("#{주소}", address);
        variables.put("#{증상}", symptoms);
        variables.put("#{엔지니어 이름}", engineerName);
        variables.put("#{엔지니어 연락처}", engineerPhone);
        variables.put("#{url}", url);

        kakaoOption.setVariables(variables);

        Message message = new Message();
        message.setFrom(CALLER_1_ID);
        message.setTo(StringFormatterUtil.cleanPhoneNumber(targetPhone));
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

    public MultipleDetailMessageSentResponse sendASComplete(String targetPhone, String receiptId, String engineerName) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(AS_COMPLETE_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{접수번호}", receiptId);
        variables.put("#{이름}", engineerName);

        kakaoOption.setVariables(variables);

        Message message = new Message();
        message.setFrom(CALLER_1_ID);
        message.setTo(StringFormatterUtil.cleanPhoneNumber(targetPhone));
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
