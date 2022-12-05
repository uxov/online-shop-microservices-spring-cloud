package xyz.defe.sp.web.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.common.pojo.ResponseData;

@FeignClient(name = "authServerClient", url = "${gateway.url}/authServer/")
public interface AuthServerFeignClient {
    @PostMapping("api/token")
    ResponseData<ApiToken> getToken(@RequestParam String uname, @RequestParam String pwd);
}
