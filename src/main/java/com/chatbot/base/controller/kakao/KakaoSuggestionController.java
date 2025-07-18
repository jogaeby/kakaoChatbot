package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.domain.event.EventService;
import com.chatbot.base.dto.BranchDto;
import com.chatbot.base.dto.SuggestionInfoDto;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.components.BasicCard;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.ItemCard;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import com.chatbot.base.view.KakaoChatBotView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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

    private final EventService eventService;
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    private final KakaoChatBotView kakaoChatBotView;

    @Value("${google.sheet.id}")
    private String SHEET_ID;

    @PostMapping(value = "input/validation")
    public ChatBotResponse validation(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String phone = chatBotRequest.getPhone();
            String comment = chatBotRequest.getComment();
            List<String> images = chatBotRequest.getImages();

            log.info("{} {} {}", phone,comment,images);


            Optional<BranchDto> branchByPhone = googleSheetUtil.getBranchByPhone(phone, SHEET_ID);

            if (branchByPhone.isEmpty()) {
                chatBotResponse.addSimpleText("제휴되지 않은 상태입니다.");
                return chatBotResponse;
            }

            BranchDto branch = branchByPhone.get();

            TextCard textCard = new TextCard();
            textCard.setDescription("해당 내용으로 건의사항 접수를 진행하시겠습니까?(궁금)\n" +
                    "\n" +
                    "※ 아래에 있는 버튼을 눌러 계속 진행하세요.");

            Carousel carousel = new Carousel();

            images.forEach(image -> {
                BasicCard basicCard = new BasicCard();
                log.info("{}",image);
                basicCard.setThumbnail(image,true);
                carousel.addComponent(basicCard);
            });


            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.addItemList("브랜드",branch.getBrandName());
            itemCard.addItemList("지점명",branch.getBranchName());
            itemCard.addItemList("연락처",phone);
            itemCard.setTitle("건의사항");
            itemCard.setDescription(comment);

            SuggestionInfoDto suggestionInfoDto = SuggestionInfoDto.builder()
                    .images(images)
                    .phone(phone)
                    .comment(comment)
                    .build();

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addCarousel(carousel);
            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"687867e59619bd57f0997a62");
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

            String id = String.valueOf(System.currentTimeMillis());

            Optional<BranchDto> branchByPhone = googleSheetUtil.getBranchByPhone(phone, SHEET_ID);

            if (branchByPhone.isEmpty()) {
                chatBotResponse.addSimpleText("제휴되지 않은 상태입니다.");
                return chatBotResponse;
            }
            BranchDto branchDto = branchByPhone.get();
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            List<Object> newRowData = new ArrayList<>();
            newRowData.add(id);
            newRowData.add(branchDto.getBrandName());
            newRowData.add(branchDto.getBranchName());
            newRowData.add(branchDto.getName());
            newRowData.add(branchDto.getPhone());
            newRowData.add(branchDto.getManagerName());
            newRowData.add(branchDto.getManagerPhone());
            newRowData.add(suggestionInfoDto.getComment());
            newRowData.add("이미지");
            newRowData.add(now);
            newRowData.add("접수");


            googleSheetUtil.appendToSheet(SHEET_ID,"건의 접수내역",newRowData);

            TextCard textCard = new TextCard();
            textCard.setDescription("\uD83D\uDCE9 접수 완료!\n" +
                    "접수번호 : "+id+"\n" +
                    "\n" +
                    "담당자가 확인 후 순차적으로 연락드립니다.\n" +
                    "빠르게 처리해드릴게요. 감사합니다!(크크)");
            chatBotResponse.addTextCard(textCard);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("접수를 실패하였습니다.");
        }
    }


}
