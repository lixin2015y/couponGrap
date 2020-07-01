package com.lee.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.Callable;

public class GrabThread implements Callable {

    private String id;

    public GrabThread(String id) {
        this.id = id;
    }

    @Override
    public Object call() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-App-Key", "dfeaf887448d0ada54b3b7c8");
            headers.set("Authorization", "Basic NDA4MTYwMTY3NjA6NmFiN2Y5MTQzZDBmNGZmODRjYzIzMWMxZmRmZGRlMjU=");
            headers.set("Host", "stats.jpush.cn");
            headers.set("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 9; PCNM00 Build/PKQ1.190630.001)");
            ResponseEntity<Map> id = restTemplate.getForEntity("http://tjzgh.bohaigaoke.com/union/mobile/eleme/getTicket/"
                    + this.id + ".jhtml?mobile=15022401101", Map.class, headers);
            return id.getBody();
        } catch (RestClientException e) {
            e.printStackTrace();
            return null;
        }
    }
}
