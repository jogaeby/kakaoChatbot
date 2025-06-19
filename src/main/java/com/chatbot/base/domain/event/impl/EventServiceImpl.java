package com.chatbot.base.domain.event.impl;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.ImageUtil;
import com.chatbot.base.common.KakaoApiService;
import com.chatbot.base.common.util.EncryptionUtil;
import com.chatbot.base.domain.event.EventService;
import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final KakaoApiService kakaoApiService;
    private final GoogleSheetUtil googleSheetUtil;
    private final AlarmTalkService alarmTalkService;

    @Value("${host.url}")
    private String HOST_URL;

    @Value("${google.sheet.id}")
    private String SHEET_ID;
    @Override
    public String asReceipt(String address, String comment, KakaoProfileDto kakaoProfile) {
        final String SHEET_PREFIX = "A/S접수";

        // 날짜 및 시트 이름 생성
        LocalDateTime now = LocalDateTime.now();
        String formattedMonth = now.format(DateTimeFormatter.ofPattern("M월"));
        String sheetName = SHEET_PREFIX + "_" + formattedMonth;
        int monthValue = now.getMonthValue(); // 1~12

        // 접수 ID 생성
        String id = monthValue + "_" + System.currentTimeMillis();


        String userName = kakaoProfile.getKakaoAccount().getName();
        String phoneNumber = kakaoProfile.getPhoneNumber();

        List<Object> newRowData = new ArrayList<>(Arrays.asList(
                id,
                "접수",
                userName,
                "'"+phoneNumber,
                address,
                comment,
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ));

        try {
            googleSheetUtil.appendToSheet(SHEET_ID, sheetName, newRowData);
            log.info("A/S 접수 성공 - ID: {}, 사용자: {}, 주소: {}", id, userName, address);
            return id;
        } catch (Exception e) {
            log.error("A/S 접수 실패 - appUserId: {}, address: {}, 오류: {}", kakaoProfile.getId(), address, e.getMessage(), e);
            throw new RuntimeException("A/S 접수 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public String inquiriesReceipt(String comment, String appUserId) {
        final String sheetName = "기타문의사항 접수내역";

        // 날짜 및 시트 이름 생성
        LocalDateTime now = LocalDateTime.now();
//        String formattedMonth = now.format(DateTimeFormatter.ofPattern("M월"));
//        String sheetName = SHEET_PREFIX + "_" + formattedMonth;
//        int monthValue = now.getMonthValue(); // 1~12

        // 접수 ID 생성
        String id = String.valueOf(System.currentTimeMillis());

        // TODO: KakaoProfileDto에서 사용자 정보 가져오기
         KakaoProfileDto kakaoProfile = kakaoApiService.getKakaoProfile(appUserId);

        String userName = kakaoProfile.getKakaoAccount().getName();
        String phoneNumber = kakaoProfile.getPhoneNumber();

        List<Object> newRowData = new ArrayList<>(Arrays.asList(
                id,
                "접수",
                userName,
                "'"+phoneNumber,
                comment,
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ));

        try {
            googleSheetUtil.appendToSheet(SHEET_ID, sheetName, newRowData);
            log.info("기타문의 접수 성공 - 접수번호: {}, 사용자: {}, 문의사항: {}", id, userName, comment);
            return id;
        } catch (Exception e) {
            log.error("기타문의 접수 실패 - appUserId: {}, address: {}, 오류: {}", appUserId, comment, e.getMessage(), e);
            throw new RuntimeException("기타문의 접수 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Async
    @Override
    public void sendReceiptAlarmTalk(String receiptId, String address, String comment, KakaoProfileDto kakaoProfile) {
        try {
            List<List<Object>> lists = googleSheetUtil.readMemberByAlarmTalkOnSheet(SHEET_ID);
            lists.forEach(objects -> {
                String managerPhone = String.valueOf(objects.get(1));

                String name = kakaoProfile.getKakaoAccount().getName();
                String phoneNumber = kakaoProfile.getPhoneNumber();
                try {
                    String encryptPhone = EncryptionUtil.encrypt(EncryptionUtil.getKey(), managerPhone);
                    String encryptReceiptId = EncryptionUtil.encrypt(EncryptionUtil.getKey(), receiptId);
                    String cleanedHostUrl = HOST_URL.replaceFirst("https?://", "");
                    String url = cleanedHostUrl + "/receipt/"+encryptPhone+"/"+encryptReceiptId
                            .replaceAll("http://","");
                    alarmTalkService.sendASReceipt(managerPhone,receiptId,name,phoneNumber,address,comment,url);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });


        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
        }
    }
    @Async
    @Override
    public void sendReceiptCompleteAlarmTalk(String receiptId, String completerName) {
        try {
            List<List<Object>> lists = googleSheetUtil.readMemberByAlarmTalkOnSheet(SHEET_ID);

            lists.forEach(objects -> {
                String managerPhone = String.valueOf(objects.get(1));
                try {
                    alarmTalkService.sendASComplete(managerPhone,receiptId,completerName);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });


        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
        }
    }
}
