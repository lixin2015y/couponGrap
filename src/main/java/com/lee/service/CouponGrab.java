package com.lee.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;

/**
 * 知工优惠券抢券
 * //http://tjzgh.bohaigaoke.com/union/mobile/eleme/caseList.jhtml?mobile=
 * //http://tjzgh.bohaigaoke.com/union/mobile/eleme/getTicket/edc6c0ad86bd438f8799a45acea793a2.jhtml?mobile=15022401101
 */
public class CouponGrab {


    /**
     * 获取抢券活动id
     *
     * @param morning 是不是上午场
     * @return
     */
    public static String getId(Boolean morning) {
        RestTemplate restTemplate = new RestTemplate();


        ResponseEntity<Map> idMap = restTemplate.getForEntity("http://tjzgh.bohaigaoke.com/union/mobile/eleme/caseList.jhtml?mobile=", Map.class);

        if (!idMap.getStatusCode().toString().contains("200")) {
            return null;
        }

        String sessions = idMap.getBody().get("_rows").toString();
        String[] split = sessions.split("}");
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains(morning ? "上午场" : "下午场")) {
                int beginIndex = split[i].indexOf("id_=") + 4;
                int endIndex = split[i].indexOf(",", beginIndex);
                return split[i].substring(beginIndex, endIndex);
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        String url = "http://tjzgh.bohaigaoke.com/union/mobile/eleme/caseList.jhtml?mobile=15022401101";
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 10; zh-cn; PCNM00 Build/QKQ1.190915.002) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/70.0.3538.80 Mobile Safari/537.36 HeyTapBrowser/40.7.6.1";
//        headers.add(HttpHeaders.USER_AGENT, USER_AGENT);
//        headers.add(HttpHeaders.HOST,"tjzgh.bohaigaoke.com");
//        headers.put("Cookie", Arrays.asList("SERVERID=1fe90eca20cd73db5ea6eb99ead1a204|1597302260|1597302259"));
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
//        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
//        System.out.println(responseEntity.getBody());
//        System.out.println(requestEntity.getHeaders());



    }
}


