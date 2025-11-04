package com.chatbot.base.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
@Slf4j
@Service
public class MailService {
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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
                    log.error("⚠️ 이미지 다운로드 실패: {}",imageUrl );
                }
            }

            // 3️⃣ 메일 전송 시도
            mailSender.send(message);
            log.info("✅ 이메일 발송 성공: {} {} ",to, subject);
            return true; // ✅ 성공 시 true 반환

        } catch (Exception e) {
            log.error("❌ 메일 전송 실패: {}",e.getMessage(),e);
            return false; // ❌ 실패 시 false 반환
        }
    }

    public boolean sendMailWithPdfAttachment(String to, String subject, String messageText, File pdfFile) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(messageText, true);
            helper.setFrom("ikpharm12@gmail.com");
            helper.addAttachment(pdfFile.getName(), new FileSystemResource(pdfFile));

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("메일 전송 실패", e);
            return false;
        }
    }

    public File convertImageUrlToPdf(String imageUrl, String id) throws Exception {
        // 이미지 다운로드
        BufferedImage image = ImageIO.read(new URL(imageUrl));

        // 투명 배경 → 흰색 배경으로 변환
        BufferedImage whiteBgImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = whiteBgImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, whiteBgImage.getWidth(), whiteBgImage.getHeight());
        g.drawImage(image, 0, 0, null);
        g.dispose();

        // 임시 PDF 파일 생성
        File pdfFile = new File(System.getProperty("java.io.tmpdir"), "prescription_" + id + ".pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
            document.addPage(page);

            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(saveTempImage(whiteBgImage), document);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, 0, 0);
            }
            document.save(pdfFile);
        }

        return pdfFile;
    }

    private File saveTempImage(BufferedImage image) throws Exception {
        File tempFile = File.createTempFile("temp_image_", ".jpg");
        ImageIO.write(image, "jpg", tempFile);
        return tempFile;
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

