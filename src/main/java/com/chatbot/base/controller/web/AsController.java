package com.chatbot.base.controller.web;

import com.chatbot.base.annotation.PassAuth;
import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.HttpService;
import com.chatbot.base.common.util.EncryptionUtil;
import com.chatbot.base.domain.event.EventService;
import com.chatbot.base.domain.member.constant.MemberRole;
import com.chatbot.base.domain.member.dto.MemberDTO;
import com.chatbot.base.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    private final EventService eventService;

    @PassAuth
    @GetMapping("{managerPhone}/{id}")
    public String getReceipt(@PathVariable String managerPhone, @PathVariable String id, Model model) {
        try {
            managerPhone = EncryptionUtil.decrypt(EncryptionUtil.getKey(), managerPhone);
            String receiptId = EncryptionUtil.decrypt(EncryptionUtil.getKey(), id);

            String sheetName;
            String category;
            if (receiptId.contains("_")) {
                String prefix = receiptId.split("_")[0]; // "1", "2", ..., "12" 등
                category = "A/S 접수내역";
                switch (prefix) {
                    case "1":
                        sheetName = "A/S접수_1월";
                        break;
                    case "2":
                        sheetName = "A/S접수_2월";
                        break;
                    case "3":
                        sheetName = "A/S접수_3월";
                        break;
                    case "4":
                        sheetName = "A/S접수_4월";
                        break;
                    case "5":
                        sheetName = "A/S접수_5월";
                        break;
                    case "6":
                        sheetName = "A/S접수_6월";
                        break;
                    case "7":
                        sheetName = "A/S접수_7월";
                        break;
                    case "8":
                        sheetName = "A/S접수_8월";
                        break;
                    case "9":
                        sheetName = "A/S접수_9월";
                        break;
                    case "10":
                        sheetName = "A/S접수_10월";
                        break;
                    case "11":
                        sheetName = "A/S접수_11월";
                        break;
                    case "12":
                        sheetName = "A/S접수_12월";
                        break;
                    default:
                        throw new RuntimeException("올바르지 않은 접수번호입니다. receiptId = "+receiptId);
                }
            } else {
                category = "기타 문의내역";
                sheetName = "기타문의사항 접수내역"; // 접두어가 없는 경우
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
            String customerName = String.valueOf(receipt.get(3));
            String customerPhone = String.valueOf(receipt.get(4));

            String inquiries = null;
            String receiptDate = null;
            String address = null;
            String symptom =null;

            if (category.equals("기타 문의내역")) {
                inquiries = String.valueOf(receipt.get(5));
                receiptDate = String.valueOf(receipt.get(6));
            }

            if (category.equals("A/S 접수내역")) {
                address = String.valueOf(receipt.get(5));
                symptom = String.valueOf(receipt.get(6));
                receiptDate = String.valueOf(receipt.get(7));
            }
            String managerName = "";
            String recentManagerPhone = "";

            if (status.equals("접수")) {
                List<Object> manager = googleSheetUtil.readMemberSheetByPhone(SHEET_ID, managerPhone);
                managerName = String.valueOf(manager.get(0));
                recentManagerPhone = String.valueOf(manager.get(1));
            }

            model.addAttribute("managerName",managerName);
            model.addAttribute("managerPhone",managerPhone);
            model.addAttribute("sheetName",sheetName);
            model.addAttribute("status", status);
            model.addAttribute("category", category);
            model.addAttribute("receiptId", receiptId); // 모델에 데이터 추가
            model.addAttribute("customerName", customerName); // 모델에 데이터 추가
            model.addAttribute("customerPhone", customerPhone);
            model.addAttribute("address", address);
            model.addAttribute("receiptDate", receiptDate);

            model.addAttribute("inquiries", inquiries);
            model.addAttribute("symptom", symptom);

            return "receipt"; // receipt.html 뷰로 이동
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            model.addAttribute("message", "접수내역을 찾을 수 없습니다."); // 모델에 데이터 추가
            return "error";
        }

    }
    @PassAuth
    @PostMapping("assign")
    public ResponseEntity receiptAssign(@RequestBody Map<String,String> data) {
        try {
            String receiptId = data.get("id");
            String sheetName = data.get("sheetName");
            String managerName = data.get("managerName");
            String managerPhone = data.get("managerPhone");
            String customerName = data.get("customerName");
            String customerPhone = data.get("customerPhone");
            String address = data.get("address");
            String symptom = data.get("symptom");

            String encryptPhone = EncryptionUtil.encrypt(EncryptionUtil.getKey(), managerPhone);
            String encryptReceiptId = EncryptionUtil.encrypt(EncryptionUtil.getKey(), receiptId);

            String url = HOST_URL + "/receipt/complete/"+encryptPhone+"/"+encryptReceiptId;

            googleSheetUtil.updateColumnsByReceiptId(SHEET_ID,sheetName,receiptId,"배정완료",managerName,"'"+managerPhone);

            alarmTalkService.sendASAssignment(managerPhone, receiptId,customerName,customerPhone,address,symptom,managerName,managerPhone,url);

            return ResponseEntity.ok()
                    .build();
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity.status(400)
                    .build();
        }
    }

    @PassAuth
    @GetMapping("/complete/{managerPhone}/{id}")
    public String getReceiptComplete(@PathVariable String managerPhone, @PathVariable String id, Model model) {
        try {
            managerPhone = EncryptionUtil.decrypt(EncryptionUtil.getKey(), managerPhone);
            String receiptId = EncryptionUtil.decrypt(EncryptionUtil.getKey(), id);

            String sheetName;
            String category;
            if (receiptId.contains("_")) {
                String prefix = receiptId.split("_")[0]; // "1", "2", ..., "12" 등
                category = "A/S 접수내역";
                switch (prefix) {
                    case "1":
                        sheetName = "A/S접수_1월";
                        break;
                    case "2":
                        sheetName = "A/S접수_2월";
                        break;
                    case "3":
                        sheetName = "A/S접수_3월";
                        break;
                    case "4":
                        sheetName = "A/S접수_4월";
                        break;
                    case "5":
                        sheetName = "A/S접수_5월";
                        break;
                    case "6":
                        sheetName = "A/S접수_6월";
                        break;
                    case "7":
                        sheetName = "A/S접수_7월";
                        break;
                    case "8":
                        sheetName = "A/S접수_8월";
                        break;
                    case "9":
                        sheetName = "A/S접수_9월";
                        break;
                    case "10":
                        sheetName = "A/S접수_10월";
                        break;
                    case "11":
                        sheetName = "A/S접수_11월";
                        break;
                    case "12":
                        sheetName = "A/S접수_12월";
                        break;
                    default:
                        throw new RuntimeException("올바르지 않은 접수번호입니다. receiptId = "+receiptId);
                }
            } else {
                category = "기타 문의내역";
                sheetName = "기타문의사항 접수내역"; // 접두어가 없는 경우
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
            String customerName = String.valueOf(receipt.get(3));
            String customerPhone = String.valueOf(receipt.get(4));

            String inquiries = null;
            String receiptDate = null;
            String address = null;
            String symptom =null;

            if (category.equals("기타 문의내역")) {
                inquiries = String.valueOf(receipt.get(5));
                receiptDate = String.valueOf(receipt.get(6));
            }

            if (category.equals("A/S 접수내역")) {
                address = String.valueOf(receipt.get(5));
                symptom = String.valueOf(receipt.get(6));
                receiptDate = String.valueOf(receipt.get(7));
            }
            String managerName = "";
            String recentManagerPhone = "";

            if (status.equals("배정완료") || status.equals("접수")) {
                List<Object> manager = googleSheetUtil.readMemberSheetByPhone(SHEET_ID, managerPhone);
                managerName = String.valueOf(manager.get(0));
                recentManagerPhone = String.valueOf(manager.get(1));
            }

            model.addAttribute("managerName",managerName);
            model.addAttribute("managerPhone",managerPhone);
            model.addAttribute("sheetName",sheetName);
            model.addAttribute("status", status);
            model.addAttribute("category", category);
            model.addAttribute("receiptId", receiptId); // 모델에 데이터 추가
            model.addAttribute("customerName", customerName); // 모델에 데이터 추가
            model.addAttribute("customerPhone", customerPhone);
            model.addAttribute("address", address);
            model.addAttribute("receiptDate", receiptDate);

            model.addAttribute("inquiries", inquiries);
            model.addAttribute("symptom", symptom);

            return "receiptComplete"; // receiptComplete.html 뷰로 이동
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            model.addAttribute("message", "접수내역을 찾을 수 없습니다."); // 모델에 데이터 추가
            return "error";
        }

    }

    @PassAuth
    @PostMapping("complete")
    public ResponseEntity receiptComplete(@RequestBody Map<String,String> data) {
        try {
            String receiptId = data.get("id");
            String sheetName = data.get("sheetName");
            String managerName = data.get("managerName");
            String managerPhone = data.get("managerPhone");
            String customerName = data.get("customerName");
            String customerPhone = data.get("customerPhone");
            String address = data.get("address");
            String symptom = data.get("symptom");

            if (receiptId.isEmpty() || managerName.isEmpty()) {
                throw new RuntimeException("잘못된 접근입니다. receiptId = "+receiptId + " managerName = " + managerName);
            }

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            googleSheetUtil.updateColumnsCAndLByReceiptId(SHEET_ID,sheetName,receiptId,"AS완료", now);

            eventService.sendReceiptCompleteAlarmTalk(receiptId,managerName);

            return ResponseEntity.ok()
                    .build();
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity.status(400)
                    .build();
        }
    }

}
