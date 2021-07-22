package xyz.defe.sp.common.rest;

import org.springframework.web.client.RestTemplate;

public class RestUtil {
    public static final RestUtil INSTANCE = new RestUtil();
    private final RestRequest restRequest = new RestRequest();

    public RestRequest set(RestTemplate rest) {
        restRequest.SetRestTemplate(rest);
        return restRequest;
    }

}
