package com.lee.schedule;

import com.lee.service.CouponGrab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class GrabJob {

    private static String morningId;

    private static String afternoonId;

    private static Logger logger = LoggerFactory.getLogger(GrabJob.class);


    @Resource
    RestTemplate restTemplate;

    // 初始化活动id
    static {
        morningId = CouponGrab.getId(true);
        afternoonId = CouponGrab.getId(false);
        System.out.println("上午id" + morningId);
        System.out.println("下午id" + afternoonId);
    }


    // 每天早上10点更新活动Id
    @Scheduled(cron = "0 0 10,16 * * ?")
    public void setId() {
        morningId = CouponGrab.getId(true);
        afternoonId = CouponGrab.getId(false);
        System.out.println("上午id" + morningId);
        System.out.println("下午id" + afternoonId);
    }

    // 抢券
    @Scheduled(cron = "55 59 10,16 * * ?")
    public void grab() {
        String id = LocalDateTime.now().getHour() < 12 ? morningId : afternoonId;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(8);
        executor.scheduleAtFixedRate(() -> CompletableFuture.runAsync(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 9; PCNM00 Build/PKQ1.190630.001)");
            ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://tjzgh.bohaigaoke.com/union/mobile/eleme/getTicket/"
                    + id + ".jhtml?mobile=15022401101", String.class, headers);
            String body = responseEntity.getBody();
            boolean isExceedMorningTime = LocalTime.now().isAfter(LocalTime.parse("11:15:00")) && LocalTime.now().isBefore(LocalTime.parse("11:30:00"));
            boolean isExceedAfternoonTime = LocalTime.now().isAfter(LocalTime.parse("17:15:00")) && LocalTime.now().isBefore(LocalTime.parse("17:30:00"));
            logger.info(body);
            if (body.contains("券已抢光") || body.contains("参与") || isExceedMorningTime || isExceedAfternoonTime) {
                logger.info("shutdown");
                executor.shutdown();
            }

        }), 0, 100, TimeUnit.MILLISECONDS);
    }
}
