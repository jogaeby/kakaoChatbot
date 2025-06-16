package com.chatbot.base.controller.kakao;

import com.chatbot.base.domain.event.EventService;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.components.ItemCard;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import com.chatbot.base.view.KakaoChatBotView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/as")
public class KakaoAsController {

    private final EventService eventService;
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    private final KakaoChatBotView kakaoChatBotView;

    @PostMapping(value = "validation")
    public ChatBotResponse authAS(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            String appUserId = chatBotRequest.getAppUserId();

            if (appUserId == null) throw new AuthenticationException("appUserId 없음");

            TextCard textCard = new TextCard();
            textCard.setDescription("아래 버튼을 눌러 A/S 접수를 진행해주세요.");
            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton(new Button("A/S접수 진행하기", ButtonAction.블럭이동,"684f669ac5b310190b722a21"));
            return chatBotResponse;

        }catch (AuthenticationException e) {
            log.error("[카카오싱크 실패] receiptReservation: {}", e.getMessage(), e);
            return kakaoChatBotView.authView();
        }catch (Exception e) {
            log.error("receiptReservation: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }
    @PostMapping(value = "address")
    public ChatBotResponse enterAddress(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String address = chatBotRequest.getAddress();
            String comment = chatBotRequest.getComment();

            StringBuilder message = new StringBuilder();
            message.append("주소: "+address)
                    .append("\n\n")
                    .append("증상: "+comment)
            ;

            chatBotResponse.addSimpleText("입력하신 내용이 아래 내용과 일치하나요?");
            chatBotResponse.addTextCard("",message.toString());


            chatBotResponse.addQuickButton("아니요",ButtonAction.블럭이동,"684f669ac5b310190b722a21");

            Button button = new Button("네,맞아요",ButtonAction.블럭이동,"684f66a7e7598b00aa826584", ButtonParamKey.address,address);
            button.setExtra(ButtonParamKey.comment,comment);
            chatBotResponse.addQuickButton(button);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("enterAddress: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "cost")
    public ChatBotResponse noticeCost(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String address = chatBotRequest.getAddressParam();
            String comment = chatBotRequest.getCommentParam();

            chatBotResponse.addSimpleText("요금 및 보증기간 안내");
            Button button = new Button("네,확인했어요",ButtonAction.블럭이동,"684f66bb47b70d2c1d6be9cf", ButtonParamKey.address,address);
            button.setExtra(ButtonParamKey.comment,comment);

            chatBotResponse.addQuickButton(button);
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

            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.addItemList("이름","홍길동");
            itemCard.addItemList("연락처","01055552222");
            itemCard.setTitle("주소");
            itemCard.setDescription(address);

            chatBotResponse.addSimpleText("해당 내용으로 A/S접수를 진행하시겠습니까?");
            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addTextCard("증상내용",comment);

            chatBotResponse.addQuickButton("다시입력하기",ButtonAction.블럭이동,"684f669ac5b310190b722a21");
            Button button = new Button("네,접수하기",ButtonAction.블럭이동,"684f66cd2c50e1482b21f7d5", ButtonParamKey.address,address);
            button.setExtra(ButtonParamKey.comment,comment);

            chatBotResponse.addQuickButton(button);
            return chatBotResponse;
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

//            String appUserId = chatBotRequest.getAppUserId();
//            if (appUserId == null) throw new AuthenticationException("appUserId 없음");
            String id = eventService.asReceipt(address, comment,"");

            ChatBotResponse chatBotResponse = new ChatBotResponse();
            chatBotResponse.addSimpleText("접수번호 [" + id + "]\n\nA/S 접수가 완료되었습니다");
            return chatBotResponse;
        }catch (RuntimeException e) {
            log.error("[카카오싱크 실패] receiptReservation: {}", e.getMessage(), e);
            return kakaoChatBotView.authView();
        }catch (Exception e) {
            log.error("receiptAs: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("제출을 실패하였습니다.");
        }
    }


}
