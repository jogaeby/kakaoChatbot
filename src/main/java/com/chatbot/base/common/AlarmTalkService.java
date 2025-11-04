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
    private final String ADMIN_GOOGLE_SHEET_URL = "docs.google.com/spreadsheets/d/12LK-mODVa9b5b8KA_m68GUF50AojwdOK7_0cok3inFM/edit?gid=1761432966#gid=1761432966";
    private final String ORDER_RECEIPT_TO_ADMIN_TEMPLATE_ID = "KA01TP2510150726438721mBM5OU52oo";
    private final String CALLER_1_ID = "010-8776-9454";
    private final String CHANNEL_ID = "KA01PF251015071911083UZDV88niwbD";
    private final String API_KEY = "NCSS6GVKW9MEQUIM";
    private final String API_SECRET_KEY = "DXSTXO67JB6IX8XAEVDUBJKRRMVH9XFW";


    public MultipleDetailMessageSentResponse sendOrderReceiptToAdmin(String targetPhone) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.solapi.com");

        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setPfId(CHANNEL_ID);
        kakaoOption.setTemplateId(ORDER_RECEIPT_TO_ADMIN_TEMPLATE_ID);
        kakaoOption.setDisableSms(true);


        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{url}", ADMIN_GOOGLE_SHEET_URL);

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
