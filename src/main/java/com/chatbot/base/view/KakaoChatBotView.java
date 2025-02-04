package com.chatbot.base.view;

import com.chatbot.base.common.StringUtil;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.common.Context;
import com.chatbot.base.dto.kakao.response.property.components.SimpleText;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
public class KakaoChatBotView {

    public ChatBotResponse memberConfirmView() {
        ChatBotResponse response = new ChatBotResponse();


        response.addSimpleText("근무신청을 진행하시겠습니까?");
        response.addQuickButton("취소",ButtonAction.메시지,"취소");
        response.addQuickButton("신청하기",ButtonAction.블럭이동,"66fbe4e579b60b3ea7aa6722");
        return response;
    }


    public ChatBotResponse reservationConfirm(String name , String phone, LocalDate reservationDate) {
        Context context = new Context("reservationInfo",1,5);
        context.addParam("name", name);
        context.addParam("phone", phone);
        context.addParam("reservationDate", reservationDate);

        ChatBotResponse chatBotResponse = new ChatBotResponse();
        // 요일 가져오기
        DayOfWeek dayOfWeek = reservationDate.getDayOfWeek();
        String day = StringUtil.getKoreanDayOfWeek(reservationDate.toString());

        StringBuilder message = new StringBuilder();
        message.append("아래 내용으로 근무를 신청 하시겠습니까?")
                .append("\n\n")
                .append("이름: " + name)
                .append("\n")
                .append("연락처: " + phone)
                .append("\n")
                .append("근무 날짜: " + reservationDate +" ("+day+")");

        SimpleText simpleText = new SimpleText(message.toString());
        chatBotResponse.addSimpleText(simpleText);
        chatBotResponse.addQuickButton("취소",ButtonAction.메시지,"");
        chatBotResponse.addQuickButton("신청",ButtonAction.블럭이동,"66fbe4ee3b1f0e08bfb88f50");
        chatBotResponse.addContext(context);
        return chatBotResponse;
    }
    public ChatBotResponse isNotMember(String kakaoUserkey) {
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        Button button = new Button("상담원 연결",ButtonAction.상담원연결,"");
        TextCard textCard = new TextCard();
        textCard.setDescription("상담원 연결하여 해당 개인코드 등록을 진행해주세요");
        textCard.setButtons(button);
        chatBotResponse.addTextCard(textCard);
        chatBotResponse.addSimpleText("개인코드: "+kakaoUserkey);
        return chatBotResponse;
    }
}
