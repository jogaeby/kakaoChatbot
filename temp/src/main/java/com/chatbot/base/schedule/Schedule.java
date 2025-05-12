package com.chatbot.base.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
@RequiredArgsConstructor
public class Schedule {

//    @Scheduled(cron = "0 * * * * *")
    public void sendBeforeAlarmTalk() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("[{}] 하루전 알람톡 실행", stopWatch.getTotalTimeSeconds());
        stopWatch.stop();
        log.info("[{}] 하루전 알람톡 종료", stopWatch.getTotalTimeSeconds());
    }

}
