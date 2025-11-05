package com.chatbot.base.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class FaxSender {
    private final String API_KEY = "NCSS6GVKW9MEQUIM";
    private final String API_SECRET_KEY = "DXSTXO67JB6IX8XAEVDUBJKRRMVH9XFW";
    private final String FAX_FROM = "010-8776-9454";
    private final String FAX_TO = "0647249454";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * ✅ 이미 업로드된 파일 ID로 팩스 전송
     */
    public boolean sendFax(String fileId, String fromFax, String toFax) {
        String url = "https://api.solapi.com/messages/v4/send-many/detail";

        try {
            // ✅ 헤더 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", createAuthHeader(API_KEY, API_SECRET_KEY));

            // ✅ 팩스 옵션 구성
            Map<String, Object> faxOptions = new HashMap<>();
            faxOptions.put("fileIds", List.of(fileId));

            // ✅ 개별 메시지 구성
            Map<String, Object> message = new HashMap<>();
            message.put("to", toFax);
            message.put("from", fromFax);
            message.put("faxOptions", faxOptions);

            // ✅ 전체 요청 바디 구성
            Map<String, Object> body = new HashMap<>();
            body.put("messages", List.of(message));

            // ✅ HTTP 요청 실행
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("📠 팩스 전송 성공! {}",response.getBody());
                return true; // ✅ 성공 시 true 반환
            } else {
                log.error("❌ 팩스 전송 실패: " + response);
                return false; // ❌ 실패 시 false 반환
            }

        } catch (Exception e) {
            log.error("❌ 팩스 전송 중 오류 발생: {}",e.getMessage(),e);
            return false; // ❌ 예외 발생 시 false 반환
        }
    }
    /**
     * 1️⃣ 이미지 URL을 Solapi Storage API로 업로드 → fileId 반환
     */
    public String uploadImageFromUrl(String imageUrl) throws Exception{
        String url = "https://api.solapi.com/storage/v1/files";
        // 1️⃣ 이미지 다운로드
        InputStream in = new URL(imageUrl).openStream();
        byte[] imageBytes = in.readAllBytes();
        String base64File = Base64.getEncoder().encodeToString(imageBytes);

        // 1️⃣ 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", createAuthHeader(API_KEY, API_SECRET_KEY));

        // 2️⃣ 요청 바디
        Map<String, Object> body = new HashMap<>();
        body.put("file", base64File);
        body.put("name", "fax_image_" + System.currentTimeMillis());
        body.put("type", "FAX");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // 3️⃣ API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().get("fileId").toString();
        } else {
            throw new RuntimeException("파일 업로드 실패: " + response);
        }
    }

    @Async
    public void uploadPdfFileAndSendFax(File pdfFile) throws Exception {
        // 1️⃣ PDF 파일 Base64 인코딩
        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        String base64File = Base64.getEncoder().encodeToString(pdfBytes);

        // 2️⃣ 헤더 설정
        String url = "https://api.solapi.com/storage/v1/files";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", createAuthHeader(API_KEY, API_SECRET_KEY));

        // 3️⃣ 요청 바디 구성
        Map<String, Object> body = new HashMap<>();
        body.put("file", base64File);
        body.put("name", pdfFile.getName());
        body.put("type", "FAX");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        // 4️⃣ Solapi API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String fileId = response.getBody().get("fileId").toString();
            log.info("✅ PDF 업로드 성공: {}",fileId);

            sendFax(fileId,FAX_FROM,FAX_TO);
        } else {
            throw new RuntimeException("❌ PDF 업로드 실패: " + response);
        }
    }

    //	"""HMAC-SHA256 시그니처 생성"""
    public static String generateSignature(String apiSecret, String dateTime, String salt)
            throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256"));
        byte[] hash = mac.doFinal((dateTime + salt).getBytes());
        return HexFormat.of().formatHex(hash);
    }
    //	"""Authorization 헤더 생성"""
    public static String createAuthHeader(String apiKey, String apiSecret) throws Exception {
        String dateTime = Instant.now().toString();
        String salt = UUID.randomUUID().toString().replace("-", "");
        String signature = generateSignature(apiSecret, dateTime, salt);

        return "HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s"
                .formatted(apiKey, dateTime, salt, signature);
    }
    /**
     * Solapi 업로드 응답 DTO
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class UploadResponse {
        @JsonProperty("fileId")
        public String fileId;
        @JsonProperty("name")
        public String name;
        @JsonProperty("url")
        public String url;
    }
}
