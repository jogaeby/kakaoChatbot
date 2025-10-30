package com.chatbot.base.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
public class StringFormatterUtil {


    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }

        // +82 또는 +82- 또는 +82  를 0으로 변환
        phoneNumber = phoneNumber.replaceFirst("^\\+82[-\\s]?", "0");

        // 모든 공백, 하이픈 제거
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
    public static String cleanPhoneNumber(String input) {
        if (input == null) {
            throw new IllegalArgumentException("전화번호가 null입니다.");
        }

        // 1. 국제전화 형식 제거: +82 -> 0
        input = input.replaceAll("^\\+82", "0");

        // 2. 괄호, 하이픈, 공백, 점 등 모든 구분 기호 제거
        input = input.replaceAll("[^0-9]", "");

        // 3. 010으로 시작하고 11자리일 경우만 유효
        if (!input.matches("^010\\d{8}$")) {
            throw new IllegalArgumentException("올바르지 않은 전화번호 형식입니다: " + input);
        }

        return input;
    }
    //100,000 -> 100000
    public static int parseIntSafe(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        // 숫자와 음수 부호만 남기기
        String cleaned = input.replaceAll("[^0-9-]", "");

        if (cleaned.isEmpty() || cleaned.equals("-")) {
            throw new NumberFormatException("No valid number in input: " + input);
        }

        return Integer.parseInt(cleaned);
    }

    public static String formatCurrency(String amount) {
        if (amount == null || amount.equals("0")) {
            return "0";
        }
        double parsedAmount = Double.parseDouble(amount);

        // 소수점 이하가 없는 경우
        if (parsedAmount == (long) parsedAmount) {
            return String.format("%,d", (long) parsedAmount);
        }

        // 소수점 이하가 있는 경우
        return String.format("%,.2f", parsedAmount);
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

    public static String formatDate(LocalDate localDate) {

        // 원하는 포맷으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN);

        return localDate.format(formatter);
    }

    public static String formatDateTime(LocalDateTime localDateTime) {
        // 원하는 포맷으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E) a hh시 mm분", Locale.KOREAN);

        return localDateTime.format(formatter);
    }

    public static String objectToString(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(object);
            log.info("JSON: {}",json);
            return json;
        }catch (JsonProcessingException e) {
            log.error("Object to JSON conversion error: {}", e.getMessage());
            return "";
        }
    }
}
