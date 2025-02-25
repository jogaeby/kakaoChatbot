package com.chatbot.base.common;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StringUtil {

    public static String formatPhoneNumber(String phoneNumber) {
        // +82를 0으로 바꾸고 나머지 부분에서 공백과 대시를 제거
        if (phoneNumber.startsWith("+82")) {
            phoneNumber = phoneNumber.replace("+82", "0");
        }
        // 공백과 대시 제거
        phoneNumber = phoneNumber.replaceAll("[\\s-]", "");
        return phoneNumber;
    }

    public static List<String> splitString(String input, int delimiter) {
        List<String> result = new ArrayList<>();

        if (input == null) {
            return result;
        }

        int length = input.length();
        int startIndex = 0;

        // 128자씩 문자열을 나눔
        while (startIndex < length) {
            int endIndex = Math.min(startIndex + delimiter, length);
            result.add(input.substring(startIndex, endIndex));
            startIndex += 128;
        }

        return result;
    }

    public static String formatCurrency(String amount) {
        try {
            // 천단위 콤마 추가
            return String.format("%,d", Long.parseLong(amount)) + "원";
        } catch (NumberFormatException e) {
            // 예외 발생 시 입력값 그대로 반환
            return amount;
        }
    }

    public static String formatAddress(String address) {
        // 입력 문자열이 null이거나 비어있으면 빈 문자열 반환
        if (address == null || address.isEmpty()) {
            return "";
        }

        // 공백을 기준으로 문자열 분리
        String[] parts = address.split(" ");

        // 첫 3개 요소 가져오기
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 3); i++) {
            result.append(parts[i]).append(" ");
        }

        // 마지막 공백 제거하고 반환
        return result.toString().trim();
    }

    public static String formatTime(String time) {
        // 입력된 시간이 4자리 숫자인지 확인
        if (time == null || time.length() != 4) {
            throw new IllegalArgumentException("올바른 시간 형식이 아닙니다. 4자리 숫자를 입력하세요.");
        }

        // 앞의 2자리는 시(hour), 뒤의 2자리는 분(minute)
        String hour = time.substring(0, 2);
        String minute = time.substring(2, 4);

        // "hh:mm" 형식으로 반환
        return hour + ":" + minute;
    }

    public static String removeHtmlTags(String htmlText) {
        if (htmlText == null || htmlText.isEmpty()) {
            return "";
        }
        // Replace HTML tags with empty string
        return htmlText.replaceAll("<[^>]*>", "").replace("&nbsp;", " ").trim();
    }

    public static String getKoreanDayOfWeek(String dateStr) {
        // 요일을 가져온 후, 한국어로 출력
        LocalDate date = LocalDate.parse(dateStr);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }
}
