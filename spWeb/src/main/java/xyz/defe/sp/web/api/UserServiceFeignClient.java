package xyz.defe.sp.web.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;

@FeignClient(name = "userServiceClient", url = "${gateway.url}/userService/")
public interface UserServiceFeignClient {
    @PostMapping("verify")
    ResponseData<Account> verify(@RequestParam String uname, @RequestParam String pwd);
}
