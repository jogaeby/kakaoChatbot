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

    public ChatBotResponse createAuthException(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        TextCard textCard = new TextCard();
        textCard.setTitle("[개인정보 수집 동의]");
        textCard.setDescription("서비스 이용을 위해 개인정보 동의가 필요합니다\n아래 버튼을 눌러 개인정보 수집 동의를 진행해주세요.");
        textCard.setButtons(new Button("동의하러 가기", ButtonAction.블럭이동,"677b1de1614a6314a21dc5d4"));
        chatBotResponse.addTextCard(textCard);
        return chatBotResponse;
    }

    public ChatBotResponse createException(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        chatBotResponse.addSimpleText("처음부터 다시 시작해주세요.");
        return chatBotResponse;
    }
}
