package xyz.defe.sp.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.user.service.AccountService;

import java.util.List;

@RestController
@ResponseDataResult
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("")
    public Object createAccounts(@RequestBody List<Account> accounts) {
        accountService.createAccounts(accounts);
        ResponseData responseData = new ResponseData();
        responseData.setMessage("created accounts successful");
        return responseData;
    }

    @PostMapping("verify")
    public Object verify(String uname, String pwd){
        return accountService.verify(uname, pwd);
    }
}
