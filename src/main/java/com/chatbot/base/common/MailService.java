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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
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


    @Async
    public void sendMailWithPdfAttachment(String to, String subject, String messageText, File file) {
        try {
            log.info("✅ 이메일 발송 시작: {} {} ",to, subject);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(messageText, true);
            helper.setFrom("ikpharm12@gmail.com");
            helper.addAttachment(file.getName(), new FileSystemResource(file));

            mailSender.send(message);
            log.info("✅ 이메일 발송 성공: {} {} ",to, subject);
        } catch (Exception e) {
            log.error("메일 전송 실패 {}", e.getMessage(),e);
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

    public File convertImageUrlToFile(String imageUrl, String id) throws IOException {
        // 이미지 다운로드
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        if (image == null) {
            throw new IOException("이미지를 불러올 수 없습니다: " + imageUrl);
        }

        // 투명 배경 → 흰색 배경으로 변환 (PNG의 투명 배경 대비)
        BufferedImage whiteBgImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = whiteBgImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, whiteBgImage.getWidth(), whiteBgImage.getHeight());
        g.drawImage(image, 0, 0, null);
        g.dispose();

        // 임시 이미지 파일 생성 (jpg or png 선택 가능)
        File outputFile = new File(System.getProperty("java.io.tmpdir"), "prescription_" + id + ".jpg");

        // BufferedImage → 파일로 저장
        ImageIO.write(whiteBgImage, "jpg", outputFile);

        return outputFile;
    }

    public File convertImageUrlToFaxTiff(String imageUrl, String id) throws IOException {
        // 1️⃣ 이미지 다운로드
        BufferedImage original = ImageIO.read(new URL(imageUrl));
        if (original == null) {
            throw new IOException("이미지를 불러올 수 없습니다: " + imageUrl);
        }

        // 2️⃣ 흰색 배경으로 변환 (투명 방지)
        BufferedImage whiteBgImage = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = whiteBgImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, whiteBgImage.getWidth(), whiteBgImage.getHeight());
        g.drawImage(original, 0, 0, null);
        g.dispose();

        // 3️⃣ 흑백 변환 (1-bit bilevel)
        BufferedImage binaryImage = new BufferedImage(
                whiteBgImage.getWidth(), whiteBgImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g2 = binaryImage.createGraphics();
        g2.drawImage(whiteBgImage, 0, 0, null);
        g2.dispose();

        // 4️⃣ TIFF로 저장 (팩스 전송용)
        File tiffFile = new File(System.getProperty("java.io.tmpdir"), "fax_" + id + ".tiff");

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("TIFF");
        if (!writers.hasNext()) {
            throw new IllegalStateException("TIFF writer를 찾을 수 없습니다. TwelveMonkeys ImageIO 라이브러리가 필요합니다.");
        }
        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(tiffFile)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            writer.write(null, new javax.imageio.IIOImage(binaryImage, null, null), param);
        } finally {
            writer.dispose();
        }

        return tiffFile;
    }

    public File convertImageUrlToTiff(String imageUrl, String id) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000); // 5초 연결 타임아웃
        connection.setReadTimeout(5000);    // 5초 읽기 타임아웃
        connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // 일부 서버는 UA 없으면 거절함

        try (InputStream inputStream = connection.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IOException("이미지를 불러올 수 없습니다: " + imageUrl);
            }

            File tiffFile = new File(System.getProperty("java.io.tmpdir"), "image_" + id + ".tiff");

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("TIFF");
            if (!writers.hasNext()) {
                throw new IllegalStateException("TIFF writer를 찾을 수 없습니다. TwelveMonkeys ImageIO 라이브러리가 필요합니다.");
            }

            ImageWriter writer = writers.next();
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(tiffFile)) {
                writer.setOutput(ios);
                ImageWriteParam param = writer.getDefaultWriteParam();
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
            } finally {
                writer.dispose();
            }

            System.out.println("✅ TIFF 변환 완료: " + tiffFile.getAbsolutePath());
            return tiffFile;
        } finally {
            connection.disconnect();
        }
    }

    public File convertResourceImageToTiff(String resourcePath, String id) throws IOException {
        // 1️⃣ resources 내부 파일 로드
        ClassPathResource resource = new ClassPathResource(resourcePath);
        if (!resource.exists()) {
            throw new IOException("리소스를 찾을 수 없습니다: " + resourcePath);
        }

        // 2️⃣ 이미지 읽기
        try (InputStream inputStream = resource.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IOException("이미지를 읽을 수 없습니다: " + resourcePath);
            }

            // 3️⃣ TIFF 저장용 임시 파일 생성
            File tiffFile = new File(System.getProperty("java.io.tmpdir"), "image_" + id + ".tiff");

            // 4️⃣ TIFF writer 가져오기 (TwelveMonkeys 필요)
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("TIFF");
            if (!writers.hasNext()) {
                throw new IllegalStateException("TIFF writer를 찾을 수 없습니다. TwelveMonkeys ImageIO 라이브러리가 필요합니다.");
            }

            ImageWriter writer = writers.next();
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(tiffFile)) {
                writer.setOutput(ios);
                ImageWriteParam param = writer.getDefaultWriteParam();
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
            } finally {
                writer.dispose();
            }
            System.out.println("✅ TIFF 변환 완료: " + tiffFile.getAbsolutePath());
            return tiffFile;
        }
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

