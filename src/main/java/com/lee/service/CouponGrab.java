package com.lee.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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


}


