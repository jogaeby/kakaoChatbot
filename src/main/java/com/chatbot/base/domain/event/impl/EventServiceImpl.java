package com.chatbot.base.domain.event.impl;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.ImageUtil;
import com.chatbot.base.common.util.KakaoApiService;
import com.chatbot.base.domain.event.EventService;
import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final KakaoApiService kakaoApiService;
    private final ImageUtil imageUtil;
    private final GoogleSheetUtil googleSheetUtil;
    @Override
    public String onePickEvent(List<String> images, String appUserId) {
        try {
            final String SHEET_ID = "1676Rgmx9eOPNTvrKJSNmnoPpnYF5dXYByvB9ZME7QFs";
            final String SHEET_NAME = "이벤트";

            String id = String.valueOf(System.currentTimeMillis());
//            KakaoProfileDto kakaoProfile = kakaoApiService.getKakaoProfile(appUserId);
//            String name = kakaoProfile.getKakaoAccount().getName();
            String name = "홍길동";
//            String nickName = kakaoProfile.getProperties().getNickname();
//            String profileImage = kakaoProfile.getProperties().getThumbnailImage();
//            String gender = kakaoProfile.getGender();
//            String birthday = kakaoProfile.getBirthDate();
//            String phone = kakaoProfile.getKakaoAccount().getPhoneNumber();
//            LocalDateTime localDateTime = LocalDateTime.now();


            StringBuilder imageUrlList = new StringBuilder();
            List<String> serverImageUrls = imageUtil.downloadImage(images,"event/onePick",name,id);
            serverImageUrls.forEach(url -> {
                imageUrlList.append(url+"\n");
            });


            List<Object> rowData = new ArrayList<>();
            rowData.add(id);
            rowData.add(name);
            rowData.add(imageUrlList);

            googleSheetUtil.appendToSheet(SHEET_ID,SHEET_NAME,rowData);
            return id;
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
