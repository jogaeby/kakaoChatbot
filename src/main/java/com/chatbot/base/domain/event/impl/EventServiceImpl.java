package com.chatbot.base.domain.event.impl;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.ImageUtil;
import com.chatbot.base.common.KakaoApiService;
import com.chatbot.base.domain.event.EventService;
import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

            String name = Optional.ofNullable(kakaoProfile.getKakaoAccount())
                    .map(acc -> acc.getName())
                    .orElse("없음");

            String phone = Optional.ofNullable(kakaoProfile.getKakaoAccount())
                    .map(acc -> acc.getPhoneNumber())
                    .orElse("없음");

            String email = Optional.ofNullable(kakaoProfile.getKakaoAccount())
                    .map(acc -> acc.getEmail())
                    .orElse("없음");

            String gender = Optional.ofNullable(kakaoProfile.getGender()).orElse("없음");
            String birthday = Optional.ofNullable(kakaoProfile.getBirthDate()).orElse("없음");

            String nickName = Optional.ofNullable(kakaoProfile.getProperties())
                    .map(p -> p.getNickname())
                    .orElse("없음");

            String profileImage = Optional.ofNullable(kakaoProfile.getProperties())
                    .map(p -> p.getThumbnailImage())
                    .orElse("없음");

//            String name = "홍길동(예시)";
//            String phone = "+82 010-1234-5678(예시)";
//            String nickName = "프로필명(예시)";
//            String profileImage = "http://yyy.kakao.com/dn/.../img_640x640.jpg(예시)";
//            String email = "sample@sample.com(예시)";
//            String gender = "남성/여성(예시)";
//            String birthday = "2022-11-30(예시)";
            List<List<Object>> rows = new ArrayList<>();
//            StringBuilder imageUrlList = new StringBuilder();
            List<String> serverImageUrls = imageUtil.downloadImage(images, LocalDate.now(),"onePick", name, id);

            serverImageUrls.forEach(image -> {
                // HYPERLINK 함수 형태로 링크 추가
                String hyperlinkFormula = String.format("=HYPERLINK(\"%s\", \"%s\")", image, image);

                List<Object> rowData = new ArrayList<>();
                rowData.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                rowData.add(id);
                rowData.add(name);
                rowData.add("'" + phone);
                rowData.add(hyperlinkFormula);
                rowData.add("");
                rowData.add("");
                rowData.add(gender);
                rowData.add(birthday);
                rowData.add(email);
                rowData.add(nickName);
                rowData.add(profileImage);

                rows.add(rowData);
            });

            googleSheetUtil.appendToSheetByAll(SHEET_ID,SHEET_NAME,rows);
            return id;
        }catch (Exception e) {
            log.error("onePickEvent 실패: appUserId:{} KakaoProfileDto 값 확인 필요 - {}", appUserId, e.getMessage(), e);
            throw new RuntimeException();
        }
    }

    @Override
    public String asReceipt(String address, String appUserId) throws GeneralSecurityException, IOException {
        try {
            final String SHEET_ID = "1xgwEkqVXh3iQBlnHN-yZIAYk5xr68pCQzCVETaRGwTw";
            final String SHEET_NAME = "A/S접수_1월";

            LocalDate now = LocalDate.now();
            String id = String.valueOf(System.currentTimeMillis());
//            KakaoProfileDto kakaoProfile = kakaoApiService.getKakaoProfile(appUserId);


            List<Object> newRowData = new ArrayList<>();
            newRowData.add(id);
            newRowData.add("홍길동");
            newRowData.add("01055554444");
            newRowData.add(address);
            newRowData.add("접수");
            newRowData.add(now.toString());

            googleSheetUtil.appendToSheet(SHEET_ID,SHEET_NAME,newRowData);

            return id;
        }catch (Exception e) {
            log.error("onePickEvent 실패: appUserId:{} KakaoProfileDto 값 확인 필요 - {}", appUserId, e.getMessage(), e);
            throw new RuntimeException();
        }
    }
}
