package xyz.defe.sp.auth.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;

@FeignClient(value = "${user-service.url}")
public interface UserFeignClient {
    @PostMapping("account/verify")
    ResponseData<Account> verify(@RequestParam String uname, @RequestParam String pwd);
}
