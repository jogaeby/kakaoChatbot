package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.FaxSender;
import com.chatbot.base.common.MailService;
import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.ChatBotValidationResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.components.BasicCard;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.SimpleImage;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/imagae")
public class KakaoImageController {

    private ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    private final FaxSender faxSender;

    private final MailService mailService;
    @PostMapping(value = "phoneNumber")
    public ChatBotValidationResponse validationCustomerPhoneNumber(@RequestBody ChatBotRequest chatBotRequest) {
        ChatBotValidationResponse chatbotResponse = new ChatBotValidationResponse();
        try {
            String customerPhoneNumber = chatBotRequest.getValue().getOrigin();

            // 1️⃣ null 체크
            if (customerPhoneNumber == null || customerPhoneNumber.trim().isEmpty()) {
                chatbotResponse.validationFail();
                return chatbotResponse;
            }

            // 2️⃣ 전화번호 정규화 (숫자만 남김)
            String normalizedNumber = customerPhoneNumber.replaceAll("[^0-9]", ""); // 숫자 외 문자 제거

            // 3️⃣ 정규식으로 검증 (010, 011, 016~019 + 총 10~11자리)
            boolean validNumber = normalizedNumber.matches("^01[0|1|6-9]\\d{3,4}\\d{4}$");

            // 4️⃣ 결과 반환
            if (validNumber) {
                chatbotResponse.validationSuccess(normalizedNumber);
            } else {
                chatbotResponse.validationFail();
            }

            return chatbotResponse;

        } catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            chatbotResponse.validationError();
            return chatbotResponse;
        }
    }
    @PostMapping("medicine/check")
    public ChatBotResponse medicineCheck(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            List<String> images = chatBotRequest.getImages();

            Carousel carousel = new Carousel();

            images.forEach(url -> {
                log.info("{}",url);
                BasicCard basicCard = new BasicCard();
                basicCard.setThumbnail(url,true);

                carousel.addComponent(basicCard);
            });
            Button button = new Button("약사 연결", ButtonAction.상담원연결,"");


            TextCard textCard = new TextCard();
            textCard.setDescription("아래 [약사 연결] 버튼을 눌러 약사 연결 후 말씀해주세요.");
            textCard.setButtons(button);

            chatBotResponse.addCarousel(carousel);
            chatBotResponse.addTextCard(textCard);
            StringFormatterUtil.objectToString(chatBotResponse);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR {}",e.getMessage(),e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping("medicine/prescription")
    public ChatBotResponse medicinePrescription(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String phone = chatBotRequest.getPhone();
            List<String> images = chatBotRequest.getImages();
            String firstImageUrl = (images != null && !images.isEmpty()) ? images.get(0) : null;
            String id = String.valueOf(System.currentTimeMillis());

            // 1️⃣ 이미지 → PDF 변환
            File pdfFile = mailService.convertImageUrlToPdf(firstImageUrl, id);

            // 2️⃣ 메일 전송 (PDF 첨부)
            mailService.sendMailWithPdfAttachment(
                    "ikpharmacy@naver.com",
                    "[" + id + "] " + phone,
                    "연락처: " + phone,
                    pdfFile
            );

            // 3️⃣ Solapi 업로드 후 팩스 전송
            faxSender.uploadPdfFileAndSendFax(pdfFile);

            // 4️⃣ 결과 처리
            Button button = new Button("약국 연결", ButtonAction.상담원연결, "");
            TextCard textCard = new TextCard();
            textCard.setDescription("[" + id + "]\n성공적으로 처방전을 제출하였습니다.\n\n" +
                    "아래 [약국 연결] 버튼을 눌른 후\n\"전송완료\"라고 말씀해주세요.");
            textCard.setButtons(button);

            chatBotResponse.addSimpleImage(firstImageUrl, "처방전");
            chatBotResponse.addTextCard(textCard);
            return chatBotResponse;

        } catch (Exception e) {
            log.error("ERROR {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("처방전 제출을 실패하였습니다.\n다시 처음부터 진행해주세요.");
        }
    }
}
