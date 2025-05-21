package com.chatbot.base.view;

import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import org.springframework.stereotype.Component;

@Component
public class KakaoChatBotView {
    public ChatBotResponse authView() {
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        TextCard textCard = new TextCard();
        Button button = new Button("네", ButtonAction.블럭이동,"68268a39d9c3e21ccc37318e");
        StringBuilder message = new StringBuilder();
        message
                .append("이벤트 응모 정보 접수를 위해")
                .append("\n")
                .append("카카오싱크 연동이 필요해요,")
                .append("\n")
                .append("\n")
                .append("계속 진행하시겠어요?")
        ;

        textCard.setDescription(message.toString());
        textCard.setButtons(button);
        chatBotResponse.addTextCard(textCard);

        return chatBotResponse;
    }
}
