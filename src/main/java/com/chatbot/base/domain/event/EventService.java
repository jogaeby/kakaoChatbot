package com.chatbot.base.domain.event;

import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;

import java.util.List;

public interface EventService {
    String onePickEvent(List<String> images, String appUserId);
}
