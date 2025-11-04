package com.chatbot.base.dto.kakao.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KakaoPluginSecureImage {
    private String privacyAgreement;
    private int imageQuantity;
    private String secureUrls;
    private String expire;

    public List<String> getImgUrlList() {
        String secureUrls = this.secureUrls;
        secureUrls = secureUrls.substring(5, secureUrls.length() - 1);

        return Arrays.stream(secureUrls.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
