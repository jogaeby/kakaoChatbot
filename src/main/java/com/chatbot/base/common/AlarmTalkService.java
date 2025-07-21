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
    private final String SUGGESTION_RECEIPT_TEMPLATE_ID = "KA01TP250718013926202fU9vtoYPbBM";


    private final String CALLER_1_ID = "010-2322-5435";
    private final String CHANNEL_ID = "KA01PF250717113750014hzmu28fxYvA";
    private final String API_KEY = "NCSJMX2VW5DE5CXS";
    private final String API_SECRET_KEY = "BWV37DJ3LKBX6BZLBYYSJBRLQ1N99MZW";


    public MultipleDetailMessageSentResponse sendSuggestionReceipt(String targetPhone, String branchName, String date, String url) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(SUGGESTION_RECEIPT_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{지점명}", branchName);
        variables.put("#{만료시간}",date);
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
}
