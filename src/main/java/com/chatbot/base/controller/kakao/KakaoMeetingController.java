package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.ImageUtil;
import com.chatbot.base.dto.SuggestionInfoDto;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.common.ListItem;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.ItemCard;
import com.chatbot.base.dto.kakao.response.property.components.ListCard;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/meeting")
public class KakaoMeetingController {
    private final GoogleSheetUtil googleSheetUtil;

    private final AlarmTalkService alarmTalkService;

    private final ImageUtil imageUtil;
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    @Value("${host.url}")
    private String HOST_URL;

    @Value("${google.sheet.id}")
    private String SHEET_ID;

    @PostMapping(value = "input/phone")
    public ChatBotResponse validation(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String phone = chatBotRequest.getPhone();
            LocalDateTime reservationDateTime = chatBotRequest.getReservationDateTime();

            SuggestionInfoDto suggestionInfoDto = SuggestionInfoDto.builder()
                    .phone(phone)
                    .date(reservationDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .build();

            TextCard textCard = new TextCard();
            textCard.setDescription("연락처와 미팅 날짜를 입력하였습니다.\n" +
                    "\n" +
                    "※ 아래에 있는 버튼을 눌러 계속 진행하세요.");
            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"687f14ef4d48f80cb481f7b7");
            chatBotResponse.addQuickButton("진행하기",ButtonAction.블럭이동,"687f14f4fb41966f133ec39c",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "input/brand")
    public ChatBotResponse brand(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            SuggestionInfoDto suggestionInfoDto = chatBotRequest.getSuggestionInfo();

            List<List<Object>> brandList = googleSheetUtil.readAllSheet(SHEET_ID, "브랜드 목록");

            // ✅ 헤더 제외
            if (brandList.size() <= 1) {
                TextCard noBrandText = new TextCard();
                noBrandText.setDescription("브랜드 목록이 없습니다.");
                chatBotResponse.addTextCard(noBrandText);
                return chatBotResponse;
            }

            TextCard textCard = new TextCard();
            textCard.setDescription("문의주실 브랜드를 선택해주세요.");
            chatBotResponse.addTextCard(textCard);

            Carousel carousel = new Carousel();
            List<List<Object>> dataList = brandList.subList(1, brandList.size());

            int total = dataList.size();
            int maxCardCount = 5;
            int maxItemPerCard = 4;
            int maxItems = maxCardCount * maxItemPerCard; // 20개
            int brandItemLimit = Math.min(total, maxItems - 1); // 19개까지 브랜드

            int dataIndex = 0;
            ListCard currentCard = new ListCard();
            currentCard.setHeader("브랜드");

            for (int i = 0; i < brandItemLimit; i++) {
                if (currentCard.getItems().size() >= maxItemPerCard) {
                    carousel.addComponent(currentCard);
                    currentCard = new ListCard();
                    currentCard.setHeader("브랜드");
                }

                List<Object> row = dataList.get(dataIndex++);

                if (row.size() <= 1 || row.get(1) == null || String.valueOf(row.get(1)).trim().isEmpty()) {
                    continue;
                }

                String no = (row.size() > 0 && row.get(0) != null) ? String.valueOf(row.get(0)) : "";
                String brandName = (row.size() > 1 && row.get(1) != null) ? String.valueOf(row.get(1)) : "";
                String thumbnail = (row.size() > 2 && row.get(2) != null) ? String.valueOf(row.get(2)) : "";

                SuggestionInfoDto newDto = SuggestionInfoDto.builder()
                        .phone(suggestionInfoDto.getPhone())
                        .date(suggestionInfoDto.getDate())
                        .brandName(brandName)
                        .build();

                ListItem item = new ListItem(brandName);
                item.setImageUrl(thumbnail);
                item.setExtra("687f14faf06e075c82f0e7f7", ButtonParamKey.suggestionInfo, newDto);

                currentCard.setItem(item);
            }

            // ✅ 기타 항목 추가
            if (currentCard.getItems().size() >= maxItemPerCard) {
                carousel.addComponent(currentCard);
                currentCard = new ListCard();
                currentCard.setHeader("브랜드");
            }

            SuggestionInfoDto etcDto = SuggestionInfoDto.builder()
                    .phone(suggestionInfoDto.getPhone())
                    .date(suggestionInfoDto.getDate())
                    .build();

            ListItem etcItem = new ListItem("기타 브랜드 문의하기");
            etcItem.setImageUrl("http://k.kakaocdn.net/dn/cZbdjj/btsPtViqGGC/XjkURg4zpSrjK6kPI2AdD0/resize.jpg");
            etcItem.setExtra("68806c24a467e1683c893df1", ButtonParamKey.suggestionInfo, etcDto);

            currentCard.setItem(etcItem);

            // ✅ 마지막 카드 추가
            carousel.addComponent(currentCard);

            chatBotResponse.addCarousel(carousel);
            return chatBotResponse;

        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }
    @PostMapping(value = "input/brand/passivity")
    public ChatBotResponse brandPassivity(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            SuggestionInfoDto suggestionInfoDto = chatBotRequest.getSuggestionInfo();
            String brandName = chatBotRequest.getBrandName();
            suggestionInfoDto.setBrandName(brandName);

            TextCard textCard = new TextCard();
            textCard.setDescription("기타 브랜드를 입력하였습니다.\n\n브랜드명: "+brandName);

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"68806c24a467e1683c893df1",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            chatBotResponse.addQuickButton("진행하기",ButtonAction.블럭이동,"687f14faf06e075c82f0e7f7",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }
    @PostMapping(value = "input/branch")
    public ChatBotResponse branch(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            SuggestionInfoDto suggestionInfoDto = chatBotRequest.getSuggestionInfo();
            String comment = chatBotRequest.getComment();
            String branchName = chatBotRequest.getBranchName();
            suggestionInfoDto.setBranchName(branchName);
            suggestionInfoDto.setComment(comment);

            TextCard textCard = new TextCard();
            textCard.setDescription("연락받아보실 연락수단을 선택해주세요");
            Button call = new Button("전화", ButtonAction.블럭이동, "687f1500a467e1683c88e7db", ButtonParamKey.suggestionInfo, suggestionInfoDto);
            call.setExtra(ButtonParamKey.choice,"전화");

            Button kakao = new Button("카카오톡",ButtonAction.블럭이동,"687f1500a467e1683c88e7db",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            kakao.setExtra(ButtonParamKey.choice,"카카오톡");

            Button message = new Button("문자",ButtonAction.블럭이동,"687f1500a467e1683c88e7db",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            message.setExtra(ButtonParamKey.choice,"문자");

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"687f14faf06e075c82f0e7f7",ButtonParamKey.suggestionInfo, suggestionInfoDto);
            chatBotResponse.addQuickButton(call);
            chatBotResponse.addQuickButton(kakao);
            chatBotResponse.addQuickButton(message);
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
            String contactChoice = chatBotRequest.getChoiceParam();
            SuggestionInfoDto suggestionInfoDto = chatBotRequest.getSuggestionInfo();
            suggestionInfoDto.setContact(contactChoice);

            String brandName = suggestionInfoDto.getBrandName();
            String branchName = suggestionInfoDto.getBranchName();
            String phone = suggestionInfoDto.getPhone();
            String comment = suggestionInfoDto.getComment();
            String contact = suggestionInfoDto.getContact();
            String date = suggestionInfoDto.getDate();
            TextCard textCard = new TextCard();
            textCard.setDescription("해당 내용으로 미팅 예약을 진행하시겠습니까?\n" +
                    "\n" +
                    "※ 아래에 있는 버튼을 눌러 계속 진행하세요.");

            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.addItemList("브랜드",brandName);
            itemCard.addItemList("지점명",branchName);
            itemCard.addItemList("연락처",phone);
            itemCard.addItemList("연락수단",contact);
            itemCard.addItemList("미팅날짜",date);

            TextCard commentTextCard = new TextCard();
            commentTextCard.setTitle("문의내용");
            commentTextCard.setDescription(comment);

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addTextCard(commentTextCard);

            chatBotResponse.addQuickButton("처음으로",ButtonAction.블럭이동,"687f14ef4d48f80cb481f7b7");
            chatBotResponse.addQuickButton("접수하기",ButtonAction.블럭이동,"687f15059619bd57f09ab69c",ButtonParamKey.suggestionInfo, suggestionInfoDto);
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
            String id = String.valueOf(System.currentTimeMillis());
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            List<Object> newRowData = new ArrayList<>();
            newRowData.add(id);
            newRowData.add("접수");
            newRowData.add(suggestionInfoDto.getBrandName());
            newRowData.add(suggestionInfoDto.getBranchName());
            newRowData.add("'"+suggestionInfoDto.getPhone());
            newRowData.add(suggestionInfoDto.getContact());
            newRowData.add(suggestionInfoDto.getComment());
            newRowData.add(suggestionInfoDto.getDate());
            newRowData.add(now);

            googleSheetUtil.appendToSheet(SHEET_ID,"미팅 예약내역",newRowData);

            List<List<Object>> admins = googleSheetUtil.readMemberSheet(SHEET_ID);
            admins.forEach(admin -> {
                String adminPhone = String.valueOf(admin.get(2));
                alarmTalkService.sendMeetingReceipt(adminPhone);
            });

            TextCard textCard = new TextCard();
            textCard.setDescription("미팅 예약이 접수 되었습니다.\n" +
                    "접수번호 : "+id+"\n" +
                    "\n" +
                    "답변까지 최대 24시간 소요될 수 있으니 참고부탁드리며 최대한 빠르게 연락드리겠습니다.:-) "
                    );
            chatBotResponse.addTextCard(textCard);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("접수를 실패하였습니다.");
        }
    }
}
