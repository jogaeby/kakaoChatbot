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
        Button button = new Button("네", ButtonAction.블럭이동,"6707147ad7cd4b2948f8b237");
        StringBuilder message = new StringBuilder();
        message
                .append("대원투어 골프 상품을 카카오톡에서 예약하시려면,")
                .append("\n")
                .append("최초 한번의 인증과 개인정보 수집 및 활용 동의가 필요해요.")
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
