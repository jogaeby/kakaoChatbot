package com.chatbot.base.view;

import com.chatbot.base.common.StringFormatterService;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.common.Context;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.SimpleText;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class KakaoChatBotView {

    public ChatBotResponse memberConfirmView() {
        ChatBotResponse response = new ChatBotResponse();


        response.addSimpleText("근무신청을 진행하시겠습니까?");
        response.addQuickButton("취소",ButtonAction.메시지,"취소");
        response.addQuickButton("신청하기",ButtonAction.블럭이동,"66fbe4e579b60b3ea7aa6722");
        return response;
    }


    public ChatBotResponse reservationConfirm(String name , String phone, LocalDate reservationDate) {
        Context context = new Context("reservationInfo",1,5);
        context.addParam("name", name);
        context.addParam("phone", phone);
        context.addParam("reservationDate", reservationDate);

        ChatBotResponse chatBotResponse = new ChatBotResponse();
        // 요일 가져오기
        DayOfWeek dayOfWeek = reservationDate.getDayOfWeek();
        String day = StringFormatterService.getKoreanDayOfWeek(reservationDate.toString());

        StringBuilder message = new StringBuilder();
        message.append("아래 내용으로 근무를 신청 하시겠습니까?")
                .append("\n\n")
                .append("이름: " + name)
                .append("\n")
                .append("연락처: " + phone)
                .append("\n")
                .append("근무 날짜: " + reservationDate +" ("+day+")");

        SimpleText simpleText = new SimpleText(message.toString());
        chatBotResponse.addSimpleText(simpleText);
        chatBotResponse.addQuickButton("취소",ButtonAction.메시지,"");
        chatBotResponse.addQuickButton("신청",ButtonAction.블럭이동,"66fbe4ee3b1f0e08bfb88f50");
        chatBotResponse.addContext(context);
        return chatBotResponse;
    }

    public ChatBotResponse getReservationList(List<Reservation> reservations) {

        ChatBotResponse chatBotResponse = new ChatBotResponse();

        if (reservations.isEmpty()) {
            chatBotResponse.addSimpleText("근무신청 내역이 없습니다.");
            return chatBotResponse;
        }
        Carousel<TextCard> carousel = new Carousel<>();

        for (Reservation reservation : reservations) {
            TextCard textCard = new TextCard();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String createDate = reservation.getCreateDate().format(formatter);

            LocalDateTime reservationDate = reservation.getReservationDate();
            LocalDate localDate = reservationDate.toLocalDate();

            StringBuilder message = new StringBuilder();
            message
                    .append("이름: " + reservation.getMember().getName())
                    .append("\n")
                    .append("연락처: " + reservation.getMember().getPhone())
                    .append("\n")
                    .append("근무 날짜: " + localDate)
                    .append("\n\n")
                    .append("신청일: " + createDate)
                    .append("\n\n")
                    .append("참여 여부: " + reservation.isJoin())
                    .append("\n\n")
                    .append("근무신청 현황: " + reservation.getStatus().getName())
            ;
            Button button = new Button("상세보기",ButtonAction.블럭이동,"66fe690ca1edf148414b7a60", ButtonParamKey.choice,reservation.getId().toString());
            textCard.setButtons(button);
            textCard.setDescription(message.toString());
            carousel.addComponent(textCard);
        }


        chatBotResponse.addCarousel(carousel);
        return chatBotResponse;
    }

    public ChatBotResponse reservationDetail(Reservation reservation) {
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        TextCard textCard = new TextCard();
        StringBuilder message = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createDate = reservation.getCreateDate().format(formatter);

        LocalDateTime reservationDate = reservation.getReservationDate();
        LocalDate localDate = reservationDate.toLocalDate();

        message
                .append("이름: " + reservation.getMember().getName())
                .append("\n")
                .append("연락처: " + reservation.getMember().getPhone())
                .append("\n")
                .append("근무 날짜: " + localDate)
                .append("\n\n")
                .append("신청일: " + createDate)
                .append("\n\n")
                .append("참여 여부: " + reservation.isJoin())
                .append("\n\n")
                .append("근무신청 현황: " + reservation.getStatus().getName())
        ;
        textCard.setDescription(message.toString());
        chatBotResponse.addTextCard(textCard);

        if (reservation.isJoin().equals("미참여") && (reservation.getStatus().equals(ReservationStatus.APPLY) || reservation.getStatus().equals(ReservationStatus.ADMISSION))) {
            chatBotResponse.addQuickButton("근무신청 취소",ButtonAction.블럭이동,"66fe6d3fde0e6d687aa60a5c", ButtonParamKey.choice,reservation.getId().toString());
        }
        chatBotResponse.addQuickButton("이전으로",ButtonAction.블럭이동,"66fbe4fb3b1f0e08bfb88f52");
        return chatBotResponse;
    }
    public ChatBotResponse kakaoUserKeyView(String kakaoUserKey) {
        ChatBotResponse chatBotResponse = new ChatBotResponse();

        chatBotResponse.addSimpleText("개인코드: "+kakaoUserKey);
        return chatBotResponse;
    }
    public ChatBotResponse reservationCancel() {
        ChatBotResponse chatBotResponse = new ChatBotResponse();

        chatBotResponse.addSimpleText("성공적으로 근무신청이 취소되었습니다.");
        return chatBotResponse;
    }

    public ChatBotResponse reservationSuccess(ReservationDto reservation) {
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        TextCard textCard = new TextCard();
        textCard.setTitle("정상적으로 "+reservation.getReservationDate()+" 날짜에 근무가 신청되었습니다.");
        StringBuilder message = new StringBuilder();

        message
                .append("이름: "+reservation.getName())
                .append("\n")
                .append("연락처: "+reservation.getPhone())
                .append("\n")
                .append("근무날짜: "+reservation.getReservationDate() + " (" +StringFormatterService.getKoreanDayOfWeek(reservation.getReservationDate())+")")
                .append("\n")
                .append("신청날짜: "+reservation.getCreateDate())
                .append("\n")
                .append("\n")
                .append("근무 시작시간이 확정되면 알림톡으로 알려드려요!")
        ;

        textCard.setDescription(message.toString());
        chatBotResponse.addTextCard(textCard);
        return chatBotResponse;
    }


    public ChatBotResponse isNotMember(String kakaoUserkey) {
        ChatBotResponse chatBotResponse = new ChatBotResponse();
        Button button = new Button("상담원 연결",ButtonAction.상담원연결,"");
        TextCard textCard = new TextCard();
        textCard.setDescription("상담원 연결하여 해당 개인코드 등록을 진행해주세요");
        textCard.setButtons(button);
        chatBotResponse.addTextCard(textCard);
        chatBotResponse.addSimpleText("개인코드: "+kakaoUserkey);
        return chatBotResponse;
    }
}
