package com.chatbot.base.dto.kakao.request;

import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.dto.kakao.sync.KakaoProfileRequestDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChatBotRequest {
    private Intent intent;
    private UserRequest userRequest;
    private Bot bot;
    private Action action;
    private Value value;
    private List<Context> contexts = new ArrayList<>();

    @Getter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Intent {
        private String id;
        private String name;
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class UserRequest {
        private String callbackUrl;
        private String timeZone;
        private Params params ;
        private Block block ;
        private String utterance;
        private String lang;
        private User user ;

        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class Params {
            private String customerName;
            private String customerPhone;
            private String productName;
            private String productDescription;
            private String productImg;
            private String productPrice;
            private String kakaoOpenChatUrl;
            private String tradingLocation;
            private String searchWord;
            private String reservationCustomerName;
            private String trackingNumber;
            private String customerProfileImage;
        }

        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class Block {
            private String id;
            private String name;
        }

        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class User {
            private String id;
            private String type;
            private Properties properties;

            @Getter
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            public class Properties {
                private String botUserKey;
                private boolean isFriend;
                private String plusfriendUserKey;
                private String bot_user_key;
                private String plusfriend_user_key;
                private String appUserStatus;
                private String appUserId;
            }
        }
    }
    @Getter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Bot
    {
        private String id;
        private String name;
    }
    @Getter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Action {
        private String name;
        private ClientExtra clientExtra;
        private Params params;
        private String id;
        private DetailParams detailParams;

        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class ClientExtra {
            private String orderId;
            private String productId;
            private String noticeId;
            private String choice;
            private String productStatus;
            private String searchWord;
            private String pageNumber;
            private String firstNumber;
            private String lastNumber;
            private ReservationDto reservation;
        }

        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class Params {
            private String name;
            private String address;
            private String phone;
            private String depart;
            private String arrive;
            private String hopePrice;
            private String profile;
            private String message;
            private String comment;
            private String images;
        }

        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class DetailParams {
            private ReservationDate visitDate;
            private ReservationDate date;
            private ReservationDate hopeCompleteDateTime;
            private ReservationDate reservationDateTime;

            @Getter
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            public class ReservationDate {
                private String groupName;
                private String origin;
                private String value;
            }
        }

    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Value {
        private String origin;
        private String resolved;
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Context {
        private String name;
        private int lifespan;
        private int ttl;
        private Map<String, ContextParamValue> params;


        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class ContextParamValue {
            private String value;
            private String resolvedValue;
        }

    }

    public String getUserKey(){
        return userRequest.getUser().getProperties().getPlusfriendUserKey();
    }

    public String getName(){
        if (Objects.isNull(action.getParams().getName())) return null;
        return action.getParams().getName();
    }

    public String getPhone(){
        if (Objects.isNull(action.getParams().getPhone())) return null;
        return action.getParams().getPhone();
    }

    public String getAddress(){
        if (Objects.isNull(action.getParams().getAddress())) return null;
        return action.getParams().getAddress();
    }

    public String getDepart(){
        if (Objects.isNull(action.getParams().getDepart())) return null;
        return action.getParams().getDepart();
    }

    public String getArrive(){
        if (Objects.isNull(action.getParams().getArrive())) return null;
        return action.getParams().getArrive();
    }

    public String getHopePrice(){
        if (Objects.isNull(action.getParams().getHopePrice())) return null;
        return action.getParams().getHopePrice();
    }

    public String getMessage(){
        if (Objects.isNull(action.getParams().getMessage())) return null;
        return action.getParams().getMessage();
    }

    public String getComment(){
        if (Objects.isNull(action.getParams().getComment())) return null;
        return action.getParams().getComment();
    }

    public LocalDate getDate() {
        if (Objects.isNull(action.getDetailParams().getDate().getOrigin())) return null;
        return LocalDate.parse(action.getDetailParams().getDate().getOrigin(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public LocalDateTime getHopeCompleteDateTime() {
        if (Objects.isNull(action.getDetailParams().getHopeCompleteDateTime().getOrigin())) return null;
        return LocalDateTime.parse(action.getDetailParams().getHopeCompleteDateTime().getOrigin(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public LocalDateTime getReservationDateTime() {
        if (Objects.isNull(action.getDetailParams().getReservationDateTime().getOrigin())) return null;
        return LocalDateTime.parse(action.getDetailParams().getReservationDateTime().getOrigin(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public String getChoiceParam(){
        if (Objects.isNull(action.getClientExtra().getChoice())) return null;
        return action.getClientExtra().getChoice();
    }

    public ReservationDto getReservation(){
        if (Objects.isNull(action.getClientExtra().getReservation())) return null;
        return action.getClientExtra().getReservation();
    }

    public String getProductId(){
        if (Objects.isNull(action.getClientExtra().getProductId())) return null;
        return action.getClientExtra().getProductId();
    }

    public int getPageNumber(){
        if (Objects.isNull(action.getClientExtra().getPageNumber())) return 0;
        return Integer.parseInt(action.getClientExtra().getPageNumber());
    }
    public String getAppUserId() {
        if (Objects.isNull(userRequest.user.properties.getAppUserId())) return null;
        return userRequest.user.properties.getAppUserId();
    }

    public KakaoProfileRequestDto getProfile() throws JsonProcessingException {
        if (Objects.isNull(action.getParams().getProfile())) return null;
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(action.getParams().getProfile(), KakaoProfileRequestDto.class);
    }

    public List<String> getImages(){
        try {
            if (Objects.isNull(action.getParams().getImages())) return null;

            ObjectMapper mapper = new ObjectMapper();
            String images = this.action.getParams().getImages();
            KakaoPluginSecureImage kakaoPluginSecureImage = mapper.readValue(images, KakaoPluginSecureImage.class);
            return kakaoPluginSecureImage.getImgUrlList();
        }catch (Exception e){
            return null;
        }
    }

    public String getUtterance(){
        return userRequest.getUtterance();
    }

    public String getRequestBlockId(){
        return userRequest.getBlock().getId();
    }

    public com.chatbot.base.dto.kakao.response.property.common.Context getOrderSheetContext() {
        Context context = this.getContexts().stream()
                .filter(ctx -> ctx.getName().equals("orderSheet"))
                .findFirst().orElse(null);

        com.chatbot.base.dto.kakao.response.property.common.Context result = new com.chatbot.base.dto.kakao.response.property.common.Context("orderSheet", 1, 5);

        context.getParams().forEach((contextParamKey, contextParamValue) -> {
            result.addParam(contextParamKey, contextParamValue.getValue());
        });

        return result;
    }
}
