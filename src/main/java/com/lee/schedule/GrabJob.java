package com.lee.schedule;

import com.lee.service.CouponGrab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
        final String USER_AGENT = "okhttp/3.14.0";
        String url = "http://tjzgh.bohaigaoke.com/union/mobile/eleme/getTicket/"
                + id + ".jhtml?mobile=18622938608";
        String url2 = "http://tjzgh.bohaigaoke.com/union/mobile/eleme/getTicket/"
                + id + ".jhtml?mobile=15022401101";
        executor.scheduleAtFixedRate(() -> CompletableFuture.runAsync(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.USER_AGENT, USER_AGENT);
            headers.add(HttpHeaders.HOST,"tjzgh.bohaigaoke.com");
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<String> body1 = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            ResponseEntity<String> body2 = restTemplate.exchange(url2, HttpMethod.GET, requestEntity, String.class);
            String body = body1.getBody();
            logger.info(body);
            logger.info(body2.getBody());
            boolean isExceedMorningTime = LocalTime.now().isAfter(LocalTime.parse("11:00:03")) && LocalTime.now().isBefore(LocalTime.parse("11:30:00"));
            boolean isExceedAfternoonTime = LocalTime.now().isAfter(LocalTime.parse("17:00:03")) && LocalTime.now().isBefore(LocalTime.parse("17:30:00"));
            if (isExceedMorningTime || isExceedAfternoonTime) {
                logger.info("shutdown");
                executor.shutdown();
            }
        }), 0, 100, TimeUnit.MILLISECONDS);
    }

}
