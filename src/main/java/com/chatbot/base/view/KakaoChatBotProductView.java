package com.chatbot.base.view;

import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.dto.ProductDTO;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.components.BasicCard;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KakaoChatBotProductView {

    public ChatBotResponse productView(Page<ProductDTO> products, String blockId) {
        List<ProductDTO> productDTOList = products.getContent().stream()
                .sorted(Comparator.comparing(ProductDTO::getCreateDate).reversed())
                .collect(Collectors.toList());

        ChatBotResponse response = new ChatBotResponse();
        Carousel<BasicCard> carousel = new Carousel<>();

        if (productDTOList.isEmpty()) {
            response.addSimpleText("현재 등록된 매물이 존재하지 않습니다.");
            return response;
        }

        productDTOList.forEach(productDTO -> {
            BasicCard basicCard = new BasicCard();
            Button linkButton = new Button("링크 바로가기", ButtonAction.웹링크연결, productDTO.getLink());
            Button detailButton = new Button("상세보기", ButtonAction.블럭이동, "67a3fb7863e1a53ac8d17145", ButtonParamKey.productId, productDTO.getId());
            detailButton.setExtra(ButtonParamKey.choice, blockId);
            StringBuilder message = new StringBuilder();
            message
                    .append("타경번호: " + productDTO.getNo())
                    .append("\n")
                    .append("물건종류: " + productDTO.getCategory())
                    .append("\n")
                    .append("소재지: " + productDTO.getLocation())
                    .append("\n")
                    .append("감정가: " + productDTO.getPrice())
                    .append("\n")
                    .append("최저가: " + productDTO.getMinPrice())
                    .append("\n")
                    .append("예상 낙찰가: " + productDTO.getExpectedPrice())
                    .append("\n")
                    .append("매각 기일: " + productDTO.getSaleDate())
                    .append("\n")
                    .append("담당자: " + productDTO.getManagerName())
                    .append("\n")
            ;

            basicCard.setThumbnail(productDTO.getImages().get(0));
            basicCard.setTitle(productDTO.getTitle());
            basicCard.setDescription(message.toString());
            basicCard.setButton(linkButton);
            basicCard.setButton(detailButton);

            carousel.addComponent(basicCard);
        });

        response.addCarousel(carousel);
        int currentPageNumber = products.getNumber();
        if (products.hasNext()) {
            int nextPageNumber = currentPageNumber + 1;

            response.addQuickButton("다음으로⮕", ButtonAction.블럭이동, blockId, ButtonParamKey.pageNumber, String.valueOf(nextPageNumber));
        }

        if (products.hasPrevious()) {
            int prevPageNumber = currentPageNumber - 1;
            response.addQuickButton("⬅이전으로", ButtonAction.블럭이동, blockId, ButtonParamKey.pageNumber, String.valueOf(prevPageNumber));
        }

        return response;
    }

    public ChatBotResponse productDetailView(ProductDTO productDTO, String blockId) {
        ChatBotResponse response = new ChatBotResponse();

        BasicCard basicCard = new BasicCard();
        Button linkButton = new Button("링크 바로가기",ButtonAction.웹링크연결,productDTO.getLink());
        StringBuilder message = new StringBuilder();
        message
                .append("타경번호: "+productDTO.getNo())
                .append("\n")
                .append("물건종류: "+productDTO.getCategory())
                .append("\n")
                .append("소재지: "+productDTO.getLocation())
                .append("\n")
                .append("감정가: "+productDTO.getPrice())
                .append("\n")
                .append("최저가: "+productDTO.getMinPrice())
                .append("\n")
                .append("예상 낙찰가: "+productDTO.getExpectedPrice())
                .append("\n")
                .append("매각 기일: "+productDTO.getSaleDate())
                .append("\n")
                .append("담당자: "+productDTO.getManagerName())
                .append("\n")
        ;

        basicCard.setThumbnail(productDTO.getImages().get(0));
        basicCard.setTitle(productDTO.getTitle());
        basicCard.setDescription(message.toString());
        basicCard.setButton(linkButton);

        response.addBasicCard(basicCard);
        response.addQuickButton("이전으로",ButtonAction.블럭이동,blockId);
        return response;
    }

    public ChatBotResponse previousProductDetailView(ProductDTO productDTO, String blockId) {
        String url = "http://211.188.58.30:8080/product/realEstate?id=";
        ChatBotResponse response = new ChatBotResponse();

        BasicCard basicCard = new BasicCard();
        Button linkButton = new Button("링크 바로가기",ButtonAction.웹링크연결,productDTO.getLink());
        Button detailButton = new Button("상세보기",ButtonAction.웹링크연결,url+productDTO.getId());
        StringBuilder message = new StringBuilder();
        message
                .append("타경번호: "+productDTO.getNo())
                .append("\n")
                .append("물건종류: "+productDTO.getCategory())
                .append("\n")
                .append("소재지: "+productDTO.getLocation())
                .append("\n")
                .append("감정가: "+productDTO.getPrice())
                .append("\n")
                .append("최저가: "+productDTO.getMinPrice())
                .append("\n")
                .append("예상 낙찰가: "+productDTO.getExpectedPrice())
                .append("\n")
                .append("매각 기일: "+productDTO.getSaleDate())
                .append("\n")
                .append("담당자: "+productDTO.getManagerName())
                .append("\n")
        ;

        basicCard.setThumbnail(productDTO.getImages().get(0));
        basicCard.setTitle(productDTO.getTitle());
        basicCard.setDescription(message.toString());
        basicCard.setButton(detailButton);
        basicCard.setButton(linkButton);

        response.addBasicCard(basicCard);
        response.addQuickButton("이전으로",ButtonAction.블럭이동,blockId);
        return response;
    }
}
