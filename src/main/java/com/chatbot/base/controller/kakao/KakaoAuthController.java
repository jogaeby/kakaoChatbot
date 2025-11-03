package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.KakaoApiService;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.sync.KakaoAccessTokenDto;
import com.chatbot.base.dto.kakao.sync.KakaoMemberTermsDto;
import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;
import com.chatbot.base.dto.kakao.sync.KakaoProfileRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.security.sasl.AuthenticationException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/auth")
public class KakaoAuthController {
    private final KakaoApiService kakaoApiService;
    private ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    @PostMapping("fallback")
    public ChatBotResponse fallback(@RequestBody ChatBotRequest chatBotRequest) {
        ChatBotResponse chatBotResponse = new ChatBotResponse();

        log.info("fallback 질문 {}",chatBotRequest.getUtterance());

        chatBotResponse.addSimpleText("");
        return chatBotResponse;
    }

    @PostMapping(value = "")
    public ChatBotResponse profile(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            KakaoProfileRequestDto profileRequestDto = chatBotRequest.getProfile();
            log.info("프로필 싱크 otp::{} appUserId::{} saved::{} message::{}", profileRequestDto.getOtp(), profileRequestDto.getAppUserId(),profileRequestDto.isSaved(),profileRequestDto.getMessage());

            if (!profileRequestDto.isSaved()) throw new AuthenticationException("카카오 싱크 인증이 되지 않았습니다. 인증이 필요합니다.");

//            TextCard textCard = new TextCard();
//            textCard.setDescription("영수증 등록을 진행해 주세요.");
//            textCard.setButtons(new Button("영수증 등록하기", ButtonAction.블럭이동,"6821606d23dc6c3328144ff7"));

//            chatBotResponse.addSimpleText("정상적으로 동의가 완료되었습니다.");
            chatBotResponse.addSimpleText("정상적으로 동의가 완료되었습니다.");
            return chatBotResponse;
        }catch (Exception e) {
            log.error("profile: {} {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createAuthException();
        }
    }

    @GetMapping("login")
    public ModelAndView login(
            @RequestParam(required = false) String code,
            @RequestParam(value = "continue", required = false) String redirect) {
        ModelAndView modelAndView = new ModelAndView();
        final String LOGIN_FAIL_PAGE = "loginError"; // loginFail.jsp 또는 loginFail.html 등의 뷰 이름

        try {
            log.info("code::{} redirect:: {}", code,redirect);
            String errorMessage = "";
            KakaoAccessTokenDto kakaoAccessToken = kakaoApiService.getKakaoAccessToken(code);
            final String accessToken = kakaoAccessToken.getAccessToken();

            KakaoProfileDto kakaoUserInfo = kakaoApiService.getKakaoUserInfoFromAccessToken(accessToken);
            KakaoMemberTermsDto kakaoUserTerms = kakaoApiService.getKakaoUserTerms(accessToken);
            return new ModelAndView("redirect:" + redirect);
//            // 대원투어 회원가입 연동
//            ResponseDto<?> isMemberResult = memberApiService.getMember(kakaoUserInfo, kakaoUserTerms);
//
//
//            // 이미 가입된 회원
//            if ("OK".equals(isMemberResult.getResult())) {
//                return new ModelAndView("redirect:" + redirect);
//            }
//
//            // 신규 회원
//            if ("ERROR".equals(isMemberResult.getResult())) {
//                // 회원가입
//                ResponseDto<?> responseDto = memberApiService.joinMember(kakaoUserInfo, kakaoUserTerms);
//
//                // 회원가입 성공
//                if ("OK".equals(responseDto.getResult())) {
//
//                }
//
//                errorMessage = responseDto.getResultMsg();
//            }
//
//            // 로그인 실패 - 실패 페이지로 이동
//            modelAndView.setViewName(LOGIN_FAIL_PAGE);
//            modelAndView.addObject("message", errorMessage);
//            return modelAndView;
        } catch (Exception e) {
            log.error("login: {} {}", e.getMessage(), e.getStackTrace());
            // 예외 발생 시 실패 페이지로 이동
            modelAndView.setViewName(LOGIN_FAIL_PAGE);
            modelAndView.addObject("message", "인증 오류가 발생했습니다.\n다시 시도해주세요.");
            return modelAndView;
        }
    }

    @PostMapping("logout")
    public ChatBotResponse logout(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            String appUserId = chatBotRequest.getAppUserId();

            String logoutId = kakaoApiService.logoutKakaoUser(appUserId);
            log.info("appUserId :: {} logoutId:: {}",appUserId,logoutId);

            if (!appUserId.equals(logoutId)) throw new AuthenticationException("아이디가 일치하지 않습니다.");

            chatBotResponse.addSimpleText("정상적으로 동의철회 되었습니다.");
            return chatBotResponse;
        } catch (AuthenticationException e) {
            log.error("logout: {} {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException("로그아웃을 실패하였습니다.");
        } catch (Exception e) {
            log.error("logout: {} {}", e.getMessage(), e.getStackTrace());
            return chatBotExceptionResponse.createException("로그아웃을 실패하였습니다.");
        }
    }
}
