package com.chatbot.base.schedule;

import com.chatbot.base.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class Schedule {
    private final ProductService productService;

    @Scheduled(cron = "0 1 0 * * *")
    public void updateProductStatus() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("[{}] 상품 상태변경 스케줄 실행",stopWatch.getTotalTimeSeconds());
        productService.updateProductStatus(LocalDate.now());
        stopWatch.stop();
        log.info("[{}] 상품 상태변경 스케줄 종료",stopWatch.getTotalTimeSeconds());
    }

}
