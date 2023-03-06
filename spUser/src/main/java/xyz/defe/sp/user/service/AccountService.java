package xyz.defe.sp.user.service;

import xyz.defe.sp.common.entity.spUser.Account;

import java.util.List;

public interface AccountService {
    void createAccounts(List<Account> accounts);

    Account verify(String uname, String pwd);

    List<Account> getAll();
}
