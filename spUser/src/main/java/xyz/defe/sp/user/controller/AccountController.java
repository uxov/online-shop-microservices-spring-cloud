package xyz.defe.sp.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.exception.ExceptionUtil;
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
    public Account verify(@RequestParam String uname, @RequestParam String pwd){
        Account account = accountService.verify(uname, pwd);
        if (account == null) {
            ExceptionUtil.warn("account verify failed");
        }
        return account;
    }

    @GetMapping("accounts")
    public List<Account> getAll() {return accountService.getAll();}
}
