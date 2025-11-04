package com.chatbot.base.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Service
public class MailService {
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMailWithInlineImages(String to, String subject, List<String> imageUrls) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // ✅ HTML 본문 생성
        StringBuilder htmlBuilder = new StringBuilder("""
            <html>
              <body style="font-family: Arial, sans-serif;">
                <h3>안녕하세요 👋</h3>
                <p>아래 이미지를 확인하세요 (URL 만료돼도 이미지가 유지됩니다)</p>
        """);

        // ✅ 각 이미지 다운로드 및 인라인 추가
        int index = 1;
        for (String imageUrl : imageUrls) {
            try (InputStream in = new URL(imageUrl).openStream()) {
                URLConnection connection = new URL(imageUrl).openConnection();
                String contentType = connection.getContentType(); // 예: image/jpeg, image/png
                byte[] imageBytes = in.readAllBytes();

                // cid를 고유하게 설정 (중복 방지)
                String cid = "img" + index;
                helper.addInline(cid, new ByteArrayResource(imageBytes),
                        contentType != null ? contentType : "image/jpeg");

                // HTML 본문에 이미지 태그 추가
                htmlBuilder.append("""
                    <div style="margin-top: 15px;">
                      <img src="cid:%s" alt="image%d"
                           style="max-width:600px; border-radius:10px; box-shadow:0 2px 8px rgba(0,0,0,0.1);"/>
                    </div>
                """.formatted(cid, index));

                index++;
            } catch (Exception e) {
                htmlBuilder.append("<p style='color:red;'>이미지 로드 실패: ")
                        .append(imageUrl)
                        .append("</p>");
            }
        }

        htmlBuilder.append("</body></html>");

        // ✅ 메일 속성 설정
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBuilder.toString(), true);
        helper.setFrom("ikpharm12@gmail.com");

        // ✅ 메일 전송
        mailSender.send(message);
    }
    public boolean sendMailWithImageAttachments(String to, String subject, String messageText, List<String> imageUrls) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 1️⃣ 메일 기본 설정
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(messageText, true);
            helper.setFrom("ikpharm12@gmail.com");

            // 2️⃣ 이미지 다운로드 및 첨부
            int index = 1;
            for (String imageUrl : imageUrls) {
                try (InputStream in = new URL(imageUrl).openStream()) {
                    URLConnection connection = new URL(imageUrl).openConnection();
                    String contentType = connection.getContentType(); // 예: image/jpeg, image/png
                    byte[] imageBytes = in.readAllBytes();

                    // 확장자 추출 (예: .jpg, .png)
                    String extension = getExtensionFromContentType(contentType);
                    String fileName = "image_" + index + extension;

                    helper.addAttachment(fileName, new ByteArrayResource(imageBytes));

                    index++;
                } catch (Exception e) {
                    System.err.println("⚠️ 이미지 다운로드 실패: " + imageUrl);
                }
            }

            // 3️⃣ 메일 전송 시도
            mailSender.send(message);
            return true; // ✅ 성공 시 true 반환

        } catch (Exception e) {
            System.err.println("❌ 메일 전송 실패: " + e.getMessage());
            e.printStackTrace();
            return false; // ❌ 실패 시 false 반환
        }
    }

    // ✅ content-type 기준으로 확장자 추출
    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) return ".jpg";
        if (contentType.contains("png")) return ".png";
        if (contentType.contains("jpeg")) return ".jpg";
        if (contentType.contains("gif")) return ".gif";
        return ".jpg";
    }
}

