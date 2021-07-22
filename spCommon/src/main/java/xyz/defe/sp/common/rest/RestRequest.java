package xyz.defe.sp.common.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.Map;

public class RestRequest {
    private RestTemplate rest;
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    void SetRestTemplate(RestTemplate rest) {
        this.rest = rest;
    }

    public ResponseData get(String url, Object... uriVariables) {
        return rest.getForEntity(url, ResponseData.class, uriVariables).getBody();
    }

    public ResponseData get(String url, ParameterizedTypeReference ptr, Object... uriVariables) {
        ResponseEntity<ResponseData> responseEntity = rest.exchange(url, HttpMethod.GET, null, ptr, uriVariables);
        return responseEntity.getBody();
    }

    public ResponseData post(String url, Object request) {
        return rest.postForEntity(url, request, ResponseData.class).getBody();
    }

    public ResponseData post(String url, Object request, ParameterizedTypeReference ptr, Object... uriVariables) {
        HttpEntity entity = new HttpEntity(request);
        ResponseEntity<ResponseData> responseEntity = rest.exchange(url, HttpMethod.POST, entity, ptr, uriVariables);
        return responseEntity.getBody();
    }

    public ResponseData post(String url, ParameterizedTypeReference ptr, Object... uriVariables) {
        ResponseEntity<ResponseData> responseEntity = rest.exchange(url, HttpMethod.POST, null, ptr, uriVariables);
        return responseEntity.getBody();
    }

    public ResponseData post(String url, Map<String, String> paramMap, ParameterizedTypeReference ptr) {
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        paramMap.entrySet().forEach(e -> map.add(e.getKey(), e.getValue()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<Map> entity = new HttpEntity<>(map, headers);
        ResponseEntity<ResponseData> responseEntity = rest.exchange(url, HttpMethod.POST, entity, ptr);
        return responseEntity.getBody();
    }
}
