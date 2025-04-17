package com.chatbot.base.dto.kakao.request;

import com.chatbot.base.domain.reservation.dto.RoomTourReservationDTO;
import com.chatbot.base.dto.kakao.sync.KakaoSyncRequestDto;
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
            private RoomTourReservationDTO roomTourReservation;
        }

        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class Params {
            private String name;
            private String phone;
            private String quantity;
            private String memo;
            private String kakaoSync;
            private String email;
            private String location;
            private String age;
            private String gender;
        }

        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class DetailParams {
            private ReservationDate visitDate;
            private ReservationDate date;

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

    public String getLocation(){
        if (Objects.isNull(action.getParams().getLocation())) return null;
        return action.getParams().getLocation();
    }

    public String getAge(){
        if (Objects.isNull(action.getParams().getAge())) return null;
        return action.getParams().getAge();
    }

    public String getGender(){
        if (Objects.isNull(action.getParams().getGender())) return null;
        return action.getParams().getGender();
    }

    public LocalDate getDate() {
        if (Objects.isNull(action.getDetailParams().getDate().getOrigin())) return null;
        return LocalDate.parse(action.getDetailParams().getDate().getOrigin(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public LocalDateTime getVisitDate() {
        if (Objects.isNull(action.getDetailParams().getVisitDate().getOrigin())) return null;
        return LocalDateTime.parse(action.getDetailParams().getVisitDate().getOrigin(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public String getChoiceParam(){
        if (Objects.isNull(action.getClientExtra().getChoice())) return null;
        return action.getClientExtra().getChoice();
    }

    public RoomTourReservationDTO getRoomTourReservation(){
        if (Objects.isNull(action.getClientExtra().getRoomTourReservation())) return null;
        return action.getClientExtra().getRoomTourReservation();
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

    public String getMemo() throws JsonProcessingException {
        if (Objects.isNull(action.getParams().getMemo())) return null;
        return action.getParams().getMemo();
    }

    public KakaoSyncRequestDto getKakaoSync() throws JsonProcessingException {
        if (Objects.isNull(action.getParams().getKakaoSync())) return null;
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(action.getParams().getKakaoSync(), KakaoSyncRequestDto.class);
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
