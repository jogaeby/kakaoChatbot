package com.chatbot.base.dto.kakao.response;

import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatBotExceptionResponse {
    private final String SERVER_HOST = "220.85.109.243";
    private final String SERVER_PORT = "8000";

    public ChatBotResponse createException(String text){
        ChatBotResponse chatBotResponse = new ChatBotResponse();

        chatBotResponse.addSimpleText(text);

        return chatBotResponse;
    }
    public ChatBotResponse createTextCardException(String text){
        ChatBotResponse chatBotResponse = new ChatBotResponse();

        chatBotResponse.addTextCard(text);

        return chatBotResponse;
    }
    public ChatBotResponse createAuthException(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        TextCard textCard = new TextCard();
        textCard.setTitle("[간편가입]");
        textCard.setDescription("서비스 이용을 위해 간편가입이 필요합니다.\n아래 버튼을 눌러 간편가입을 진행해주세요.");
        textCard.setButtons(new Button("간편가입 하러가기", ButtonAction.블럭이동,"68de385147a9e61d1ae66a34"));
        chatBotResponse.addTextCard(textCard);
        return chatBotResponse;
    }

    public ChatBotResponse createException(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        chatBotResponse.addSimpleText("처음부터 다시 시작해주세요.");
        return chatBotResponse;
    }
}
