package com.chatbot.base.dto.kakao.response;

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

    public ChatBotResponse createWaitImageException(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        String imageUrl = "http://"+SERVER_HOST+":"+SERVER_PORT+"/images/ChatbotWait.jpeg";
        log.info(imageUrl);
        chatBotResponse.addSimpleImage(imageUrl,"...");

        return chatBotResponse;
    }
    public ChatBotResponse createException(){
        ChatBotResponse chatBotResponse = new ChatBotResponse();

        chatBotResponse.addSimpleText("시스템에 오류가 발생하였습니다.\n처음부터 다시 시작해주세요.");

        return chatBotResponse;
    }

}
