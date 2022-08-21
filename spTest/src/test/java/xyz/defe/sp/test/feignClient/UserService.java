package xyz.defe.sp.test.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;

@FeignClient(name = "userServiceClient", url = "http://localhost:9002/userService/")
public interface UserService {
    @PostMapping("verify")
    ResponseData<Account> verify(@RequestParam String uname, @RequestParam String pwd);
}
