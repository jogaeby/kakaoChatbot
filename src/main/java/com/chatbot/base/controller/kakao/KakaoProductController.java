package com.chatbot.base.controller.kakao;

import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.product.dto.ProductDTO;
import com.chatbot.base.domain.product.service.ProductService;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.view.KakaoChatBotProductView;
import com.chatbot.base.view.KakaoChatBotView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/products")
public class KakaoProductController {
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();
    private final KakaoChatBotProductView kakaoChatBotProductView;
    private final ProductService productService;

    @PostMapping(value = "test")
    public void test(@RequestBody String chatBotRequest) {
        log.info("{}", chatBotRequest);
    }

    @PostMapping(value = "today")
    public ChatBotResponse getProductsByToday(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            List<ProductDTO> productList = productService.getProductList(ProductStatus.DISPLAY);
            return kakaoChatBotProductView.productView(productList,"67a3fb6e38e4386089f9fa44");
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "dayBefore")
    public ChatBotResponse getProductsByDayBefore(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            List<ProductDTO> productList = productService.getProductList(ProductStatus.PRE_DISPLAY);
            return kakaoChatBotProductView.productView(productList,"67a3ff73cf3be837a5176214");
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "detail")
    public ChatBotResponse getProductDetail(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            String productId = chatBotRequest.getProductId();
            String preBlockId = chatBotRequest.getChoiceParam();
            ProductDTO productDTO = productService.getProduct(productId);

            return kakaoChatBotProductView.productDetailView(productDTO,preBlockId);
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return chatBotExceptionResponse.createException();
        }
    }
}
