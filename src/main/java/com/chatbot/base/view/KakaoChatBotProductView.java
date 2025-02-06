package com.chatbot.base.view;

import com.chatbot.base.common.StringUtil;
import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.dto.ProductDTO;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.common.Context;
import com.chatbot.base.dto.kakao.response.property.components.BasicCard;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.SimpleText;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
public class KakaoChatBotProductView {

    public ChatBotResponse productView(List<ProductDTO> productDTOList,String blockId) {
        ChatBotResponse response = new ChatBotResponse();
        Carousel<BasicCard> carousel = new Carousel<>();

        if (productDTOList.isEmpty()) {
            response.addSimpleText("현재 등록된 매물이 존재하지 않습니다.");
            return response;
        }

        productDTOList.forEach(productDTO -> {
            BasicCard basicCard = new BasicCard();
            Button linkButton = new Button("링크",ButtonAction.웹링크연결,productDTO.getLink());
            Button detailButton = new Button("상세보기",ButtonAction.블럭이동,"67a3fb7863e1a53ac8d17145", ButtonParamKey.productId,productDTO.getId());
            detailButton.setExtra(ButtonParamKey.choice,blockId);

            basicCard.setThumbnail(productDTO.getImages().get(0));
            basicCard.setTitle(productDTO.getTitle());
            basicCard.setDescription(productDTO.getDescription());
            basicCard.setButton(linkButton);
            basicCard.setButton(detailButton);

            carousel.addComponent(basicCard);
        });

        response.addCarousel(carousel);
        return response;
    }

    public ChatBotResponse productDetailView(ProductDTO productDTO, String blockId) {
        ChatBotResponse response = new ChatBotResponse();

        BasicCard basicCard = new BasicCard();
        Button linkButton = new Button("매물 상세보기",ButtonAction.웹링크연결,productDTO.getLink());

        basicCard.setThumbnail(productDTO.getImages().get(0));
        basicCard.setTitle(productDTO.getTitle());
        basicCard.setDescription(productDTO.getDescription());
        basicCard.setButton(linkButton);

        response.addBasicCard(basicCard);
        response.addQuickButton("이전으로",ButtonAction.블럭이동,blockId);
        return response;
    }
}
