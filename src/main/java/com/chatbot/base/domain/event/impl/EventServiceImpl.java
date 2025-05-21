package com.chatbot.base.domain.event.impl;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.ImageUtil;
import com.chatbot.base.common.KakaoApiService;
import com.chatbot.base.domain.event.EventService;
import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
            KakaoProfileDto kakaoProfile = kakaoApiService.getKakaoProfile(appUserId);
            String name = kakaoProfile.getKakaoAccount().getName();
            String nickName = kakaoProfile.getProperties().getNickname();
            String profileImage = kakaoProfile.getProperties().getThumbnailImage();
            String email = kakaoProfile.getKakaoAccount().getEmail();
            String gender = kakaoProfile.getGender();
            String birthday = kakaoProfile.getBirthDate();
            String phone = kakaoProfile.getKakaoAccount().getPhoneNumber();

//            String name = "홍길동(예시)";
//            String phone = "+82 010-1234-5678(예시)";
//            String nickName = "프로필명(예시)";
//            String profileImage = "http://yyy.kakao.com/dn/.../img_640x640.jpg(예시)";
//            String email = "sample@sample.com(예시)";
//            String gender = "남성/여성(예시)";
//            String birthday = "2022-11-30(예시)";
            List<List<Object>> rows = new ArrayList<>();
//            StringBuilder imageUrlList = new StringBuilder();
            List<String> serverImageUrls = imageUtil.downloadImage(images, "onePick", name, id);

            serverImageUrls.forEach(image -> {
                List<Object> rowData = new ArrayList<>();
                rowData.add(id);
                rowData.add(name);
                rowData.add("'" + phone);
                rowData.add("");
                rowData.add(nickName);
                rowData.add(profileImage);
                rowData.add(email);
                rowData.add(gender);
                rowData.add(birthday);

                // HYPERLINK 함수 형태로 링크 추가
                String hyperlinkFormula = String.format("=HYPERLINK(\"%s\", \"%s\")", image, image);
                rowData.add(hyperlinkFormula);

                rowData.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

                rows.add(rowData);
            });

            googleSheetUtil.appendToSheetByAll(SHEET_ID,SHEET_NAME,rows);
            return id;
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            throw new RuntimeException();
        }
    }
}
