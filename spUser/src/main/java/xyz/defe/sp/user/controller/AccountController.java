package xyz.defe.sp.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("accounts")
    public ResponseData createAccounts(@RequestBody List<Account> accounts) {
        accountService.createAccounts(accounts);
        return new ResponseData().setMessage("created accounts successful");
    }

    @PostMapping("account/verify")
    public Account verify(String uname, String pwd){
        return accountService.verify(uname, pwd);
    }

    @GetMapping("accounts")
    public List<Account> getAll() {return accountService.getAll();}
}
