package com.chatbot.base.common;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GoogleSheetUtilTest {
    @Autowired
    private GoogleSheetUtil googleSheetUtil;

    @Test
    void addEvent() throws IOException {
        String calendarId = "60f84fd663dae4f04f8ab4a9603c4cbc32bc065c3e66580abb55f33c5aface68@group.calendar.google.com";
        Event event = new Event()
                .setSummary("회의")
                .setLocation("서울")
                .setDescription("팀 회의")
                .setStart(new EventDateTime().setDateTime(new DateTime("2025-04-01T10:00:00+09:00")).setTimeZone("Asia/Seoul"))
                .setEnd(new EventDateTime().setDateTime(new DateTime("2025-04-01T11:00:00+09:00")).setTimeZone("Asia/Seoul"));

        googleSheetUtil.insertEvent(calendarId,event);
    }

    @Test
    void readAllSheet() throws GeneralSecurityException, IOException {
        List<List<Object>> lists = googleSheetUtil.readAllSheet("12LK-mODVa9b5b8KA_m68GUF50AojwdOK7_0cok3inFM", "상품목록");
        lists.forEach(objects -> {
            Object o = objects.get(0);
            Object o1 = objects.get(1);

            System.out.println("o = " + o + " "+o1);
        });
    }

    @Test
    void appendToSheet() throws GeneralSecurityException, IOException {
        List<Object> rowData = new ArrayList<>();
        rowData.add("test");
        googleSheetUtil.appendToSheet("1676Rgmx9eOPNTvrKJSNmnoPpnYF5dXYByvB9ZME7QFs","이벤트",rowData);
    }

    @Test
    void name() throws GeneralSecurityException, IOException {

        googleSheetUtil.updateColumnsByReceiptId("1xgwEkqVXh3iQBlnHN-yZIAYk5xr68pCQzCVETaRGwTw","기타문의사항 접수내역","1750070749532","배정","테스트","01077554433");
    }

    @Test
    void readMemberByAlarmTalkOnSheet() throws GeneralSecurityException, IOException {
        List<List<Object>> lists = googleSheetUtil.readMemberByAlarmTalkOnSheet("1XU_k61ZxQZ7PPaaOcjiEeGJAqAniL91piZvFsGP63Uc");
        lists.forEach(objects -> {
            System.out.println("objects = " + objects.get(3));
        });
    }

    @Test
    void readMemberSheet() throws GeneralSecurityException, IOException {
        List<List<Object>> lists = googleSheetUtil.readMemberSheet("1XU_k61ZxQZ7PPaaOcjiEeGJAqAniL91piZvFsGP63Uc");

        lists.forEach(objects -> {
            System.out.println("objects = " + objects.get(2));
        });
    }
}