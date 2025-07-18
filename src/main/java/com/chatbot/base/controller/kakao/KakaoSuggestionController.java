package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.KakaoApiService;
import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.event.EventService;
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
import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;
import com.chatbot.base.view.KakaoChatBotView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
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

            List<List<Object>> members = googleSheetUtil.readMemberByAlarmTalkOnSheet(SHEET_ID);
            // 시트에서 전화번호가 존재하는지 확인
            Optional<List<Object>> matchedRow = members.stream()
                    .filter(row -> {
                        if (row.size() > 4 && row.get(4) != null) {
                            String sheetPhone = row.get(4).toString();
                            return StringFormatterUtil.formatPhoneNumber(sheetPhone)
                                    .equals(StringFormatterUtil.formatPhoneNumber(phone));
                        }
                        return false;
                    })
                    .findFirst(); // 가장 첫 번째 매칭되는 row만 찾음 (Optional 반환)

            if (matchedRow.isEmpty()) {
                chatBotResponse.addSimpleText("제휴되지 않은 상태입니다.");
                return chatBotResponse;
            }

            List<Object> branch = matchedRow.get();

            TextCard textCard = new TextCard();
            textCard.setDescription("해당 내용으로 건의사항 접수를 진행하시겠습니까?(궁금)\n" +
                    "\n" +
                    "※ 아래에 있는 버튼을 눌러 계속 진행하세요.");

            Carousel carousel = new Carousel();

//            images.forEach(image -> {
//                BasicCard basicCard = new BasicCard();
//                basicCard.setThumbnail(image);
//                carousel.addComponent(branch);
//            });

            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.addItemList("브랜드",String.valueOf(branch.get(1)));
            itemCard.addItemList("지점명",String.valueOf(branch.get(2)));
            itemCard.addItemList("연락처",phone);
            itemCard.setTitle("건의사항");
            itemCard.setDescription(comment);

            chatBotResponse.addTextCard(textCard);
//            chatBotResponse.addCarousel(carousel);

            chatBotResponse.addItemCard(itemCard);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "address")
    public ChatBotResponse enterAddress(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String address = chatBotRequest.getAddress();
            String comment = chatBotRequest.getComment();
            TextCard textCard = new TextCard();
            textCard.setDescription("\uD83E\uDDFE A/S 요금 및 보증기간 안내입니다.\n" +
                    "\n" +
                    "⭐계약기간 내라면 대부분 무상 처리됩니다!\n\n" +
                    "⭐단, 고객 부주의나 소모품 문제는 유상일 수 있어요.\n\n" +
                    "⭐계약기간이 끝난 경우엔 출장비나 수리비가 발생할 수 있고, 방문 후 정확한 비용 안내드릴게요.\n" +
                    "\n" +
                    "\uD83D\uDEE0\uFE0F A/S 접수를 계속하시려면  \n" +
                    "\"네, 확인했어요\" 버튼을 눌러주세요.(흡족)");
            chatBotResponse.addTextCard(textCard);
            Button firstMenuButton = new Button("처음으로",ButtonAction.블럭이동,"684ff639b721652da7a7ce99");
            Button button = new Button("네,확인했어요",ButtonAction.블럭이동,"684f66bb47b70d2c1d6be9cf", ButtonParamKey.address,address);
            button.setExtra(ButtonParamKey.comment,comment);
            chatBotResponse.addQuickButton(button);
            chatBotResponse.addQuickButton(firstMenuButton);

            return chatBotResponse;
        }catch (Exception e) {
            log.error("enterAddress: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "confirm")
    public ChatBotResponse finalConfirm(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String address = chatBotRequest.getAddressParam();
            String comment = chatBotRequest.getCommentParam();
            String appUserId = chatBotRequest.getAppUserId();

            if (appUserId == null) throw new AuthenticationException("appUserId 없음");
            TextCard textCard = new TextCard();
            textCard.setDescription("해당 내용으로 A/S접수를 진행하시겠습니까?(궁금)\n" +
                    "\n" +
                    "※ 아래에 있는 버튼을 눌러 계속 진행하세요.");
            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.addItemList("이름","");
            itemCard.addItemList("연락처","");
            itemCard.setTitle("주소");
            itemCard.setDescription(address);

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addTextCard("증상내용",comment);
            Button firstMenuButton = new Button("처음으로",ButtonAction.블럭이동,"684ff639b721652da7a7ce99");
            Button button = new Button("네,접수하기",ButtonAction.블럭이동,"684f66cd2c50e1482b21f7d5", ButtonParamKey.address,address);
            button.setExtra(ButtonParamKey.comment,comment);

            chatBotResponse.addQuickButton("다시입력하기",ButtonAction.블럭이동,"684f669ac5b310190b722a21");
            chatBotResponse.addQuickButton(button);
            chatBotResponse.addQuickButton(firstMenuButton);
            return chatBotResponse;
        }catch (AuthenticationException e) {
            log.error("[카카오싱크 실패] receiptReservation: {}", e.getMessage(), e);
            return kakaoChatBotView.authView();
        }catch (Exception e) {
            log.error("finalConfirm: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }


    @PostMapping(value = "receipt")
    public ChatBotResponse receiptAs(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            String address = chatBotRequest.getAddressParam();
            String comment = chatBotRequest.getCommentParam();

            String appUserId = chatBotRequest.getAppUserId();
            if (appUserId == null) throw new AuthenticationException("appUserId 없음");
            // TODO: KakaoProfileDto에서 사용자 정보 가져오기


//            eventService.sendReceiptAlarmTalk(receiptId,address,comment,kakaoProfile);


            ChatBotResponse chatBotResponse = new ChatBotResponse();
            Button firstMenuButton = new Button("처음으로",ButtonAction.블럭이동,"684ff639b721652da7a7ce99");
            TextCard textCard = new TextCard();
            textCard.setDescription("\uD83D\uDCE9 접수 완료!\n" +
                    "접수번호 : "+"\n" +
                    "\n" +
                    "담당자가 확인 후 순차적으로 연락드립니다.\n" +
                    "빠르게 처리해드릴게요. 감사합니다!(크크)");

            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton(firstMenuButton);
            return chatBotResponse;
        }catch (AuthenticationException e) {
            log.error("[카카오싱크 실패] receiptReservation: {}", e.getMessage(), e);
            return kakaoChatBotView.authView();
        }catch (Exception e) {
            log.error("receiptAs: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("접수를 실패하였습니다.");
        }
    }


}
