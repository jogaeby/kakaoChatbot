package com.chatbot.base.common;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.Calendar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class GoogleSheetUtil {
    private static final String CREDENTIALS_FILE_PATH = "etc/GoogleCredential.json";
    private static final String APPLICATION_NAME = "google-sheet-project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS,);
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_EVENTS);
    private Sheets sheetsService;
    // GoogleCredentials를 사용하여 자격 증명 가져오기

    private GoogleCredentials getCredentials() throws IOException {
        try (InputStream inputStream = new ClassPathResource(CREDENTIALS_FILE_PATH).getInputStream()) {
            return GoogleCredentials.fromStream(inputStream).createScoped(SCOPES);
        }
    }

    // Google Calendar API 서비스 초기화
    private Calendar getCalendarService() throws IOException {
        return new Calendar.Builder(new NetHttpTransport(), JSON_FACTORY, new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // 이벤트를 추가하는 메서드
    public void insertEvent(String calendarId, Event event) throws IOException {
        Calendar service = getCalendarService();
        event = service.events().insert(calendarId, event).execute();
        System.out.println("이벤트가 등록되었습니다: " + event.getHtmlLink());
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        if (sheetsService == null) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new ClassPathResource(CREDENTIALS_FILE_PATH).getInputStream())
                    // Google API를 호출할 떄 필요한 권한을 지정하는 부분 , 읽기/쓰기 권한을 나타냄
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));
            sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
        return sheetsService;
    }
    // 구글 시트의 모든 데이터를 읽어오기
    public void readAllSheet(String spreadSheetId, String sheetName) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport(); // HTTP 요청에 사용될 트랜스포트

        // Sheets API 클라이언트 빌드
        Sheets service = getSheetsService();
        // 시트 데이터를 읽어오기
        String range = sheetName; // 읽을 범위 설정
        ValueRange response = service.spreadsheets().values().get(spreadSheetId, range).execute();
        List<List<Object>> values = response.getValues();

        // 읽은 데이터가 없으면 로그 출력
        if (values == null || values.isEmpty()) {
            log.info("지정된 시트에 데이터가 없습니다.");
        } else {
            // 데이터 출력
            values.forEach(row -> {
                row.forEach(cell -> log.info("{}", cell));
            });
        }
    }

    public void appendToSheet(String spreadSheetId, String sheetName, List<Object> newRowData) throws GeneralSecurityException, IOException {
        log.info("{} {}",spreadSheetId,sheetName);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport(); // HTTP 요청에 사용될 트랜스포트

        // Sheets API 클라이언트 빌드
        Sheets service = getSheetsService();

        // 현재 시트 데이터를 읽어서 최대 no 값을 계산
        String range = sheetName + "!A:A"; // 첫 번째 열 전체 범위
        ValueRange response = service.spreadsheets().values().get(spreadSheetId, range).execute();
        List<List<Object>> values = response.getValues();

        int maxNo = 0;
        if (values != null && !values.isEmpty()) {
            for (List<Object> row : values) {
                if (!row.isEmpty()) {
                    try {
                        int currentNo = Integer.parseInt(row.get(0).toString());
                        maxNo = Math.max(maxNo, currentNo); // 최대값 갱신
                    } catch (NumberFormatException e) {
                        log.warn("숫자로 변환할 수 없는 값: {}", row.get(0)); // 숫자가 아닌 값은 무시
                    }
                }
            }
        }

        // 새 데이터의 첫 번째 값으로 maxNo + 1 추가
        newRowData.add(0, maxNo + 1);

        // 데이터를 추가할 범위 설정
        String appendRange = sheetName + "!A1"; // 데이터 추가 범위

        // 추가할 데이터를 ValueRange로 변환
        ValueRange body = new ValueRange()
                .setValues(List.of(newRowData)); // 데이터를 리스트 형태로 전달
        log.info("Formatted data: {}", newRowData);
        // 데이터를 시트에 Append
        AppendValuesResponse appendResponse = service.spreadsheets().values()
                .append(spreadSheetId, appendRange, body)
                .setValueInputOption("USER_ENTERED") // 사용자가 입력한 것처럼 처리
                .execute();

        log.info("데이터 추가 완료 (추가된 셀 수): {}", appendResponse.getUpdates().getUpdatedCells());

//        int lastRow = maxNo; // 마지막 추가된 행 번호 (0-based index)
//        // newRowData 기반으로 체크박스 열 계산
//        int startCol = newRowData.size(); // 마지막 열의 인덱스
//        int endCol = startCol + 1;        // 체크박스 범위
//
//        addCheckboxToSheet(service, spreadSheetId, sheetName, lastRow+1, lastRow + 2, startCol, endCol); // C열에 체크박스 추가
    }

    public void appendToSheetByAll(String spreadSheetId, String sheetName, List<List<Object>> newRows) throws GeneralSecurityException, IOException {
        log.info("{} {}", spreadSheetId, sheetName);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport(); // HTTP 요청에 사용될 트랜스포트

        // Sheets API 클라이언트 빌드
        Sheets service = getSheetsService();

        // 현재 시트 데이터를 읽어서 최대 no 값을 계산
        String range = sheetName + "!A:A"; // 첫 번째 열 전체 범위
        ValueRange response = service.spreadsheets().values().get(spreadSheetId, range).execute();
        List<List<Object>> values = response.getValues();

        int maxNo = 0;
        if (values != null && !values.isEmpty()) {
            for (List<Object> row : values) {
                if (!row.isEmpty()) {
                    try {
                        int currentNo = Integer.parseInt(row.get(0).toString());
                        maxNo = Math.max(maxNo, currentNo); // 최대값 갱신
                    } catch (NumberFormatException e) {
                        log.warn("숫자로 변환할 수 없는 값: {}", row.get(0)); // 숫자가 아닌 값은 무시
                    }
                }
            }
        }

        // 각 행의 첫 번째 칸에 고유 번호 추가
        for (int i = 0; i < newRows.size(); i++) {
            newRows.get(i).add(0, maxNo + 1 + i);
        }

        // 데이터를 추가할 범위 설정
        String appendRange = sheetName + "!A1"; // 데이터 추가 범위

        // 추가할 데이터를 ValueRange로 변환
        ValueRange body = new ValueRange()
                .setValues(newRows); // 여러 행 데이터 입력

        log.info("Formatted data: {}", newRows);

        // 데이터를 시트에 Append
        AppendValuesResponse appendResponse = service.spreadsheets().values()
                .append(spreadSheetId, appendRange, body)
                .setValueInputOption("USER_ENTERED") // 사용자가 입력한 것처럼 처리
                .execute();

        log.info("데이터 추가 완료 (추가된 셀 수): {}", appendResponse.getUpdates().getUpdatedCells());
    }

    private void addCheckboxToSheet(Sheets service, String spreadSheetId, String sheetName, int startRow, int endRow, int startCol, int endCol) throws IOException {
        // 시트 ID 가져오기
        Spreadsheet spreadsheet = service.spreadsheets().get(spreadSheetId).execute();
        Sheet targetSheet = spreadsheet.getSheets().stream()
                .filter(sheet -> sheet.getProperties().getTitle().equals(sheetName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sheet name not found: " + sheetName));
        int sheetId = targetSheet.getProperties().getSheetId();

        // 체크박스 추가 요청 생성
        Request checkboxRequest = new Request().setRepeatCell(new RepeatCellRequest()
                .setRange(new GridRange()
                        .setSheetId(sheetId)
                        .setStartRowIndex(startRow)
                        .setEndRowIndex(endRow)
                        .setStartColumnIndex(startCol)
                        .setEndColumnIndex(endCol))
                .setCell(new CellData()
                        .setUserEnteredValue(new ExtendedValue().setBoolValue(false)) // 초기값은 false로 설정
                        .setDataValidation(new DataValidationRule()
                                .setCondition(new BooleanCondition().setType("BOOLEAN")) // Boolean 조건 설정
                                .setStrict(true))) // 엄격한 데이터 유효성 검사
                .setFields("dataValidation,userEnteredValue")); // 유효성 검사와 초기값 필드 설정

        // 요청 실행
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(checkboxRequest));
        service.spreadsheets().batchUpdate(spreadSheetId, batchUpdateRequest).execute();

        log.info("체크박스가 추가되었습니다. 범위: rows {}-{}, cols {}-{}", startRow, endRow, startCol, endCol);
    }
}
