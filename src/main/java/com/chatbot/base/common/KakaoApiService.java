package com.chatbot.base.common;

import com.chatbot.base.dto.kakao.sync.KakaoAccessTokenDto;
import com.chatbot.base.dto.kakao.sync.KakaoAddressDto;
import com.chatbot.base.dto.kakao.sync.KakaoMemberTermsDto;
import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class KakaoApiService {
    private RestTemplate restTemplate = new RestTemplate();
    private final String REST_API_KEY = "241354c76f9bca97b335ff5eed7eed82";
    private final String ADMIN_API_KEY = "501504b775e89e23272ff9045a55cb07";
    private final String CLIENT_SECRET = "MS9HmJQZgA25D6Z3Llhs5VFyOpIHFauP";
    private final String REDIRECT_URI = "http://223.130.157.107:28080/kakao/chatbot/auth/login";
    private ObjectMapper objectMapper = new ObjectMapper();

    public boolean isAgree(String appUserId) {
        log.info("카카오 개인정보 제공 동의 여부 appUserId = {} ",appUserId);
        //사용자가 개인정보 제공에 동의하는 경우에 한해 해당 사용자의 appUserId를 확인할 수 있습니다.
        if (Objects.nonNull(appUserId)) {
            return true;
        }
        return false;
    }

    public KakaoProfileDto getKakaoProfile(String appUserId) {
        final String url = "https://kapi.kakao.com/v2/user/me?target_id_type=user_id&target_id="+appUserId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","KakaoAK "+ADMIN_API_KEY);
        HttpEntity<String> httpEntity = new HttpEntity<>("",headers);
        ResponseEntity<KakaoProfileDto> exchange = restTemplate.exchange(url, HttpMethod.POST,httpEntity, KakaoProfileDto.class);
        return exchange.getBody();
    }

    public KakaoAccessTokenDto getKakaoAccessToken(String code) {
        final String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type","application/x-www-form-urlencoded;");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", REST_API_KEY);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("code", code);
        body.add("client_secret", CLIENT_SECRET);


        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);

        ResponseEntity<KakaoAccessTokenDto> exchange = restTemplate.postForEntity(url,httpEntity, KakaoAccessTokenDto.class);
        KakaoAccessTokenDto response = exchange.getBody();
        try {
            String responseStr = objectMapper.writeValueAsString(response);
            log.info("[{}] response::{}","카카오 토큰 받기 응답",responseStr);

        }catch (Exception e) {
            log.info("[{}] response::{}","카카오 토큰 받기 응답",e.getStackTrace());
        }
        return response;
    }

    public KakaoProfileDto getKakaoUserInfoFromAccessToken(String accessToken) {
        final String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type","application/x-www-form-urlencoded;");
        headers.set("Authorization","Bearer "+accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);

        ResponseEntity<KakaoProfileDto> exchange = restTemplate.exchange(url,HttpMethod.GET,httpEntity, KakaoProfileDto.class);
        KakaoProfileDto response = exchange.getBody();
        try {
            String responseStr = objectMapper.writeValueAsString(response);
            log.info("[{}] response::{}","카카오 사용자 정보 가져오기 응답",responseStr);

        }catch (Exception e) {
            log.info("[{}] response::{}","카카오 사용자 정보 가져오기 응답",e.getStackTrace());
        }

        return response;
    }

    public KakaoProfileDto getKakaoUserInfoFromUserId(String userId) {
        final String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type","application/x-www-form-urlencoded;");
        headers.set("Authorization","KakaoAK "+ADMIN_API_KEY);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id",userId);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);

        ResponseEntity<KakaoProfileDto> exchange = restTemplate.postForEntity(url,httpEntity, KakaoProfileDto.class);
        KakaoProfileDto response = exchange.getBody();
        try {
            String responseStr = objectMapper.writeValueAsString(response);
            log.info("[{}] response::{}","카카오 사용자 정보 가져오기 응답",responseStr);

        }catch (Exception e) {
            log.info("[{}] response::{}","카카오 사용자 정보 가져오기 응답",e.getStackTrace());
        }
        return response;
    }

    public KakaoAddressDto getKakaoUserAddressFromUserId(String userId) {
        final String url = "https://kapi.kakao.com/v1/user/shipping_address?target_id_type=user_id&target_id="+userId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","KakaoAK "+ADMIN_API_KEY);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);

        ResponseEntity<KakaoAddressDto> exchange = restTemplate.exchange(url,HttpMethod.GET,httpEntity, KakaoAddressDto.class);
        KakaoAddressDto response = exchange.getBody();
        try {
            String responseStr = objectMapper.writeValueAsString(response);
            log.info("[{}] response::{}","카카오 사용자 주소 가져오기 응답",responseStr);

        }catch (Exception e) {
            log.info("[{}] response::{}","카카오 사용자 주소 가져오기 응답",e.getStackTrace());
        }
        return response;
    }

    public KakaoMemberTermsDto getKakaoUserTerms(String accessToken) {
        final String url = "https://kapi.kakao.com/v2/user/service_terms";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type","application/x-www-form-urlencoded;");
        headers.set("Authorization","Bearer "+accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);

        ResponseEntity<KakaoMemberTermsDto> exchange = restTemplate.exchange(url,HttpMethod.GET,httpEntity, KakaoMemberTermsDto.class);
        KakaoMemberTermsDto response = exchange.getBody();

        try {
            objectMapper.registerModule(new JavaTimeModule()); // Java 8 time module 등록
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식 사용

            String responseStr = objectMapper.writeValueAsString(response);
            log.info("[{}] response::{}","카카오 서비스 약관 동의 내역 확인하기 응답",responseStr);

        }catch (Exception e) {
            log.info("[{}] response::{}","카카오 서비스 약관 동의 내역 확인하기 응답",e.getStackTrace());
        }

        return response;
    }

    public String logoutKakaoUser(String userId) {
        final String url = "https://kapi.kakao.com/v1/user/unlink";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type","application/x-www-form-urlencoded;");
        headers.set("Authorization","KakaoAK "+ADMIN_API_KEY);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id",userId);


        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);

        ResponseEntity<Map> exchange = restTemplate.postForEntity(url,httpEntity, Map.class);
        Map response = exchange.getBody();

        try {
            String responseStr = objectMapper.writeValueAsString(response);
            log.info("[{}] response::{}","카카오 로그아웃 응답",responseStr);

        }catch (Exception e) {
            log.info("[{}] response::{}","카카오 로그아웃 응답",e.getStackTrace());
        }

        return String.valueOf(exchange.getBody().get("id"));
    }
}
