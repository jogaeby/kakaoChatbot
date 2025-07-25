package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.ImageUtil;
import com.chatbot.base.common.PasswordUtil;
import com.chatbot.base.common.util.EncryptionUtil;
import com.chatbot.base.dto.BranchDto;
import com.chatbot.base.dto.SuggestionInfoDto;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.components.BasicCard;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.ItemCard;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/suggestion")
public class KakaoSuggestionController {
    private final GoogleSheetUtil googleSheetUtil;

    private final AlarmTalkService alarmTalkService;

    private final ImageUtil imageUtil;
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    @Value("${host.url}")
    private String HOST_URL;

    @Value("${google.sheet.id}")
    private String SHEET_ID;

    @PostMapping(value = "input/validation")
    public ChatBotResponse validation(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String phone = chatBotRequest.getPhone();

            Optional<BranchDto> branchByPhone = googleSheetUtil.getBranchByPhone(phone, SHEET_ID);

            if (branchByPhone.isEmpty()) {
                chatBotResponse.addSimpleText("제휴되지 않은 상태입니다.");
                return chatBotResponse;
            }

            BranchDto branch = branchByPhone.get();

            TextCard textCard = new TextCard();
            textCard.setDescription("지점 확인 되었습니다.\n" +
                    "\n" +
                    "※ 아래에 있는 버튼을 눌러 계속 진행하세요.");

            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.addItemList("브랜드",branch.getBrandName());
            itemCard.addItemList("지점명",branch.getBranchName());
            itemCard.addItemList("연락처",phone);

            SuggestionInfoDto suggestionInfoDto = SuggestionInfoDto.builder()
                    .brandName(branch.getBrandName())
                    .branchName(branch.getBranchName())
                    .phone(phone)
                    .build();

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"687867e59619bd57f0997a62");
            chatBotResponse.addQuickButton("진행하기",ButtonAction.블럭이동,"687ed30c4d48f80cb481e9f4",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "input/comment")
    public ChatBotResponse comment(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            SuggestionInfoDto suggestionInfoDto = chatBotRequest.getSuggestionInfo();
            String comment = chatBotRequest.getComment();
            suggestionInfoDto.setComment(comment);
            suggestionInfoDto.setImages(new ArrayList<>());

            TextCard textCard = new TextCard();
            textCard.setDescription("건의사항을 입력하였습니다.\n" +
                    "건의사항에 대한 사진 등록이 필요하신 경우 사진을 등록 부탁드립니다."+
                    "\n\n" +
                    "※ 아래에 있는 버튼을 눌러 계속 진행하세요.");

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"687ed30c4d48f80cb481e9f4",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            chatBotResponse.addQuickButton("사진등록하기",ButtonAction.블럭이동,"687ed33b9619bd57f09aa97a",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            chatBotResponse.addQuickButton("사진등록 없이 진행하기",ButtonAction.블럭이동,"687ed684b8541d6f084c1515",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "input/images")
    public ChatBotResponse images(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            SuggestionInfoDto suggestionInfoDto = chatBotRequest.getSuggestionInfo();
            List<String> images = chatBotRequest.getImages();
            suggestionInfoDto.setImages(images);

            TextCard textCard = new TextCard();
            textCard.setDescription("사진을 추가하였습니다.\n" +
                    "\n" +
                    "※ 아래에 있는 버튼을 눌러 계속 진행하세요.");

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"687ed33b9619bd57f09aa97a",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            chatBotResponse.addQuickButton("진행하기",ButtonAction.블럭이동,"687ed684b8541d6f084c1515",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "input/final")
    public ChatBotResponse finalConfirm(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            SuggestionInfoDto suggestionInfoDto = chatBotRequest.getSuggestionInfo();
            List<String> images = suggestionInfoDto.getImages();
            String brandName = suggestionInfoDto.getBrandName();
            String branchName = suggestionInfoDto.getBranchName();
            String phone = suggestionInfoDto.getPhone();
            String comment = suggestionInfoDto.getComment();

            TextCard textCard = new TextCard();
            textCard.setDescription("해당 내용으로 건의사항 접수를 진행하시겠습니까?\n" +
                    "\n" +
                    "※ 아래에 있는 버튼을 눌러 계속 진행하세요.");



            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.addItemList("브랜드",brandName);
            itemCard.addItemList("지점명",branchName);
            itemCard.addItemList("연락처",phone);
            itemCard.setTitle("건의사항");
            itemCard.setDescription(comment);


            chatBotResponse.addTextCard(textCard);

            if (!images.isEmpty()) {
                Carousel carousel = new Carousel();

                images.forEach(image -> {
                    BasicCard basicCard = new BasicCard();
                    log.info("{}",image);
                    basicCard.setThumbnail(image,true);
                    carousel.addComponent(basicCard);
                });
                chatBotResponse.addCarousel(carousel);
            }

            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addQuickButton("처음으로",ButtonAction.블럭이동,"687867e59619bd57f0997a62");
            chatBotResponse.addQuickButton("접수하기",ButtonAction.블럭이동,"6879f63cfb41966f133dec84",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "receipt")
    public ChatBotResponse receiptAs(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            SuggestionInfoDto suggestionInfoDto = chatBotRequest.getSuggestionInfo();
            String phone = suggestionInfoDto.getPhone();
            List<String> images = suggestionInfoDto.getImages();
            String id = String.valueOf(System.currentTimeMillis());

            Optional<BranchDto> branchByPhone = googleSheetUtil.getBranchByPhone(phone, SHEET_ID);

            if (branchByPhone.isEmpty()) {
                chatBotResponse.addSimpleText("제휴되지 않은 상태입니다.");
                return chatBotResponse;
            }
            StringBuilder imageUrlsSt = new StringBuilder();
            List<String> imageUrls = imageUtil.downloadImage(images,"suggestion","건의사항",id);
            imageUrls.forEach(url -> {
                imageUrlsSt.append(url)
                        .append("\n")
                ;
            });


            BranchDto branchDto = branchByPhone.get();
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            List<Object> newRowData = new ArrayList<>();
            newRowData.add(id);
            newRowData.add("접수");
            newRowData.add(branchDto.getBrandName());
            newRowData.add(branchDto.getBranchName());
            newRowData.add(branchDto.getName());
            newRowData.add(branchDto.getPhone());
            newRowData.add(branchDto.getManagerName());
            newRowData.add(branchDto.getManagerPhone());
            newRowData.add(suggestionInfoDto.getComment());
            newRowData.add(imageUrlsSt.toString());
            newRowData.add("");
            newRowData.add(now);
            newRowData.add("");


            LocalDateTime expiredDateTime = LocalDateTime.now().plusDays(1);


            googleSheetUtil.appendToSheet(SHEET_ID,"건의 접수내역",newRowData);

            String BaseUrl = HOST_URL.replaceAll("http://", "");
            BaseUrl = BaseUrl+"/receipt/suggestion/"+ EncryptionUtil.encrypt(EncryptionUtil.getKey(),id);

            alarmTalkService.sendSuggestionReceipt(branchDto.getManagerPhone(),branchDto.getBranchName(),expiredDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),BaseUrl);

            TextCard textCard = new TextCard();
            textCard.setDescription("접수 완료!\n" +
                    "접수번호 : "+id+"\n" +
                    "\n" +
                    "담당자가 확인 후 빠르게 조치하겠습니다.\n" +
                    "감사합니다!(크크)");
            chatBotResponse.addTextCard(textCard);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("접수를 실패하였습니다.");
        }
    }
}
