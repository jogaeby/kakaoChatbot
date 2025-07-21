package com.chatbot.base.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ImageUtil {
    @Value("${host.ip}")
    private String HOST_IP;

    @Value("${host.port}")
    private String HOST_PORT;

    private final String BASE_DIR = "images";

    public String saveFile(MultipartFile file, String type, String receiptNumber) throws IOException {
        byte[] bytes = file.getBytes();

        String fileName = receiptNumber+"_"+file.getOriginalFilename();

        // 저장 경로: BASE_DIR/suggestion/{receiptNumber}/actionCompleted
        Path directoryPath = Paths.get(BASE_DIR, type, receiptNumber, "actionCompleted");
        Path filePath = directoryPath.resolve(fileName);

        // 디렉토리 생성 (없으면)
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // 파일 저장
        Files.write(filePath, bytes);

        // 접근 가능한 URL 반환
        return HOST_IP + ":" + HOST_PORT + "/images/suggestion/" + receiptNumber + "/actionCompleted/" + fileName;
    }


    // 이미지 다운로드 메서드
    public List<String> downloadImage(List<String> imgUrlList, String dirName, String imageName, String receiptNumber) {
        final String EXT = ".jpg";  // 확장자 설정
        List<String> downloadImgUrlList = new LinkedList<>();

        try {
            // 기본 디렉토리 경로
            Path baseDirPath = Paths.get(BASE_DIR + File.separator + dirName);
            if (!Files.exists(baseDirPath)) {
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-xr-x");
                Files.createDirectories(baseDirPath, PosixFilePermissions.asFileAttribute(permissions));
            }
        } catch (Exception e) {
            log.error("베이스 이미지 디렉토리 생성 실패");
            e.printStackTrace();
        }

        // 이미지 다운로드 처리
        for (int i = 0; i < imgUrlList.size(); i++) {
            int cnt = i + 1;
            String fileName = cnt + "_" + receiptNumber + "_" + imageName + EXT;
            String destinationDir = BASE_DIR + File.separator + dirName + File.separator + receiptNumber;

            // 이미지 다운로드
            try {
                downloadUrlImage(imgUrlList.get(i), destinationDir, fileName);
            } catch (Exception e) {
                log.error("이미지 다운로드 실패: {}", imgUrlList.get(i));
                e.printStackTrace();
                downloadImgUrlList.add("이미지 다운로드 실패");
                continue;
            }

            // 다운로드한 파일 확인 및 URL 반환
            File file = new File(destinationDir + File.separator + fileName);
            if (file.exists()) {
                downloadImgUrlList.add(HOST_IP+":"+HOST_PORT + "/images" + "/" + dirName + "/" + receiptNumber + "/" + fileName);
            } else {
                downloadImgUrlList.add("이미지 실패");
            }
        }

        return downloadImgUrlList;
    }

    public List<String> downloadImage(List<String> imgUrlList, LocalDate date, String dirName, String imageName, String receiptNumber) {
        final String EXT = ".jpg";
        List<String> downloadImgUrlList = new LinkedList<>();

        try {
            // 날짜 폴더 포함한 경로
            Path baseDirPath = Paths.get(BASE_DIR, dirName, date.toString());
            if (!Files.exists(baseDirPath)) {
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-xr-x");
                Files.createDirectories(baseDirPath, PosixFilePermissions.asFileAttribute(permissions));
            }
        } catch (Exception e) {
            log.error("베이스 이미지 디렉토리 생성 실패");
            e.printStackTrace();
        }

        for (int i = 0; i < imgUrlList.size(); i++) {
            int cnt = i + 1;
            String fileName = cnt + "_" + receiptNumber + "_" + imageName + EXT;
            String destinationDir = BASE_DIR + File.separator + dirName + File.separator + date + File.separator + receiptNumber;

            try {
                downloadUrlImage(imgUrlList.get(i), destinationDir, fileName);
            } catch (Exception e) {
                log.error("이미지 다운로드 실패: {}", imgUrlList.get(i));
                e.printStackTrace();
                downloadImgUrlList.add("이미지 다운로드 실패");
                continue;
            }

            File file = new File(destinationDir + File.separator + fileName);
            if (file.exists()) {
                String imageUrl = HOST_IP + ":" + HOST_PORT + "/images/" + dirName + "/" + date + "/" + receiptNumber + "/" + fileName;
                downloadImgUrlList.add(imageUrl);
            } else {
                downloadImgUrlList.add("이미지 실패");
            }
        }

        return downloadImgUrlList;
    }
    public void downloadUrlImage(String imageUrl, String destinationDir, String fileName) throws Exception {
        URL url = new URL(imageUrl);
        Path dirPath = Paths.get(destinationDir);
        Path filePath = dirPath.resolve(fileName);

        // 부모 디렉토리를 생성합니다.
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 이미지를 다운로드하여 지정된 파일 경로에 저장합니다.
        try (InputStream in = url.openStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void deleteOrderImages(String orderNumber) {
        Path path = Paths.get(BASE_DIR+"/"+orderNumber);
        if (Files.exists(path)) {
            try {
                deleteDirectoryRecursively(path);
            }catch (Exception e) {
                log.error("이미지 삭제 실패 {}",orderNumber);
            }
        }
    }

    public void deleteDirectoryRecursively(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 파일 삭제
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                // 디렉토리 내 모든 파일이 삭제된 후 디렉토리 삭제
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // 파일 방문 실패 시 예외를 던져서 오류를 알림
                throw exc;
            }
        });
    }
}