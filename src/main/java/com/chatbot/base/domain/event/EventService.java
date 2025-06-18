package com.chatbot.base.domain.event;

import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface EventService {
    String asReceipt(String address,String comment, KakaoProfileDto kakaoProfile) throws GeneralSecurityException, IOException;

    String inquiriesReceipt(String comment, String appUserId) throws GeneralSecurityException, IOException;
    void sendReceiptAlarmTalk(String receiptId, String address, String comment, KakaoProfileDto kakaoProfile);

    void sendReceiptCompleteAlarmTalk(String receiptId, String completerName);
}
