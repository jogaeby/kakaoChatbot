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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/inquiries")
public class KakaoInquiriesController {

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
            textCard.setDescription("아래 버튼을 눌러 문의사항 접수를 진행해주세요.");
            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton(new Button("문의사항접수 진행하기", ButtonAction.블럭이동,"684f672c47b70d2c1d6be9db"));
            return chatBotResponse;

        }catch (AuthenticationException e) {
            log.error("[카카오싱크 실패] receiptReservation: {}", e.getMessage(), e);
            return kakaoChatBotView.authView();
        }catch (Exception e) {
            log.error("receiptReservation: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }
    @PostMapping(value = "comment")
    public ChatBotResponse enterAddress(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String comment = chatBotRequest.getComment();

            StringBuilder message = new StringBuilder();
            message.append("문의사항: "+comment)
            ;

            chatBotResponse.addSimpleText("입력하신 내용이 아래 내용과 일치하나요?");
            chatBotResponse.addTextCard("",message.toString());


            chatBotResponse.addQuickButton("아니요,다시입력",ButtonAction.블럭이동,"684f672c47b70d2c1d6be9db");

            Button button = new Button("네,맞아요",ButtonAction.블럭이동,"684f682a938bdf47fcf4d701", ButtonParamKey.comment,comment);

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
            String comment = chatBotRequest.getCommentParam();

            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.addItemList("이름","홍길동");
            itemCard.addItemList("연락처","01055552222");
            itemCard.setTitle("문의사항");
            itemCard.setDescription(comment);

            chatBotResponse.addSimpleText("해당 내용으로 문의사항을 접수를 진행하시겠습니까?");
            chatBotResponse.addItemCard(itemCard);

            chatBotResponse.addQuickButton("다시입력하기",ButtonAction.블럭이동,"684f672c47b70d2c1d6be9db");
            Button button = new Button("네,접수하기",ButtonAction.블럭이동,"684f6832c5b310190b722a54", ButtonParamKey.comment,comment);

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
            String comment = chatBotRequest.getCommentParam();

            String appUserId = chatBotRequest.getAppUserId();
            if (appUserId == null) throw new AuthenticationException("appUserId 없음");

            String id = eventService.inquiriesReceipt(comment,appUserId);

            ChatBotResponse chatBotResponse = new ChatBotResponse();
            chatBotResponse.addSimpleText("접수번호 [" + id + "]\n\n문의사항 접수가 완료되었습니다");
            return chatBotResponse;
        }catch (AuthenticationException e) {
            log.error("[카카오싱크 실패] receiptReservation: {}", e.getMessage(), e);
            return kakaoChatBotView.authView();
        }catch (Exception e) {
            log.error("receiptAs: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("제출을 실패하였습니다.");
        }
    }


}
