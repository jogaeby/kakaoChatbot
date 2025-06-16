package com.chatbot.base.domain.event;

import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface EventService {
    String onePickEvent(List<String> images, String appUserId);

    String asReceipt(String address, String appUserId) throws GeneralSecurityException, IOException;
}
