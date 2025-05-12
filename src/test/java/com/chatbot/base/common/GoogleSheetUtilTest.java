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
        googleSheetUtil.readAllSheet("1676Rgmx9eOPNTvrKJSNmnoPpnYF5dXYByvB9ZME7QFs","이벤트");
    }

    @Test
    void name() throws GeneralSecurityException, IOException {
        List<Object> rowData = new ArrayList<>();
        rowData.add("test");
        googleSheetUtil.appendToSheet("1676Rgmx9eOPNTvrKJSNmnoPpnYF5dXYByvB9ZME7QFs","이벤트",rowData);
    }
}