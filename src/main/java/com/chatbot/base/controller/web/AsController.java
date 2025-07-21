package com.chatbot.base.controller.web;

import com.chatbot.base.annotation.PassAuth;
import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.ImageUtil;
import com.chatbot.base.common.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("receipt")
public class AsController {
    @Value("${google.sheet.id}")
    private String SHEET_ID;

    @Value("${host.url}")
    private String HOST_URL;

    private final GoogleSheetUtil googleSheetUtil;

    private final AlarmTalkService alarmTalkService;

    private final ImageUtil imageUtil;

    @PassAuth
    @GetMapping("{type}/{id}")
    public String getReceipt(@PathVariable String type, @PathVariable String id, Model model) {
        try {
            String receiptId = EncryptionUtil.decrypt(EncryptionUtil.getKey(), id);
            String sheetName;
            String title;
            switch (type) {
                case "suggestion":
                    sheetName = "건의 접수내역";
                    title = "건의 접수";
                    break;
                case "2":
                    sheetName = "문의 접수내역";
                    title = "문의 접수";
                    break;
                default:
                    throw new RuntimeException("올바르지 않은 접수번호입니다. receiptId = "+receiptId);
            }

            List<List<Object>> receiptList = googleSheetUtil.readAllSheet(SHEET_ID, sheetName);

            List<List<Object>> reversedList = new ArrayList<>(receiptList);
            Collections.reverse(reversedList);

            List<Object> receipt = reversedList.stream()
                    .filter(row -> row.size() > 1 && receiptId.equals(row.get(1)))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("접수내역을 찾을 수 없습니다. receiptId = " + receiptId));

            String recentReceiptId = String.valueOf(receipt.get(1));
            String status = String.valueOf(receipt.get(2));
            String brandName = String.valueOf(receipt.get(3));
            String branchName = String.valueOf(receipt.get(4));
            String managerName = String.valueOf(receipt.get(7));
            String comment = String.valueOf(receipt.get(9));
            String images = String.valueOf(receipt.get(10));
            List<String> imageUrls = Arrays.stream(images.split("\\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            String receiptDateTime = String.valueOf(receipt.get(12));
            String completedDateTime = (receipt.size() > 13 && receipt.get(13) != null)
                    ? String.valueOf(receipt.get(13))
                    : "";
            log.info("{} {} {} {} {} {}",recentReceiptId,images,receiptDateTime,status,comment,managerName);

            model.addAttribute("title",title);
            model.addAttribute("type",sheetName);
            model.addAttribute("status", status);
            model.addAttribute("receiptId", receiptId); // 모델에 데이터 추가
            model.addAttribute("brandName", brandName); // 모델에 데이터 추가
            model.addAttribute("branchName", branchName);
            model.addAttribute("managerName", managerName);
            model.addAttribute("comment", comment);
            model.addAttribute("imageUrls", imageUrls);
            model.addAttribute("receiptDateTime", receiptDateTime);
            model.addAttribute("completedDateTime", completedDateTime);

            return "receipt"; // receipt.html 뷰로 이동
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            model.addAttribute("message", "접수내역을 찾을 수 없습니다."); // 모델에 데이터 추가
            return "error";
        }

    }
    @PassAuth
    @PostMapping("assign")
    public ResponseEntity receiptAssign(@RequestParam("id") String id,
                                        @RequestParam("type") String type,
                                        @RequestParam("images") List<MultipartFile> images) {
        try {
            log.info("{} {} {}",id,type,images);
            List<String> imageUrls = new ArrayList<>();
            images.forEach(file -> {
                try {
                    String url = imageUtil.saveFile(file, type, id);
                    imageUrls.add(url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            StringBuilder imageUrlsStr = new StringBuilder();

            imageUrls.forEach(s -> {
                imageUrlsStr.append(s)
                        .append("\n");
            });

            googleSheetUtil.updateColumnsCAndLByReceiptId(SHEET_ID,type,id,"조치완료",imageUrlsStr.toString(),LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            return ResponseEntity.ok()
                    .build();
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity.status(400)
                    .build();
        }
    }
}
