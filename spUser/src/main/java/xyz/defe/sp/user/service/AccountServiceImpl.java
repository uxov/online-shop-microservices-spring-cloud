package xyz.defe.sp.user.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.user.dao.AccountDao;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;

    @Override
    public void createAccounts(List<Account> accounts) {
        accountDao.saveAll(accounts);
    }

    @Override
    public Account verify(String uname, String pwd) {
        if (StringUtils.isBlank(uname) || StringUtils.isBlank(pwd)) {
            ExceptionUtil.warn("uname or pwd is empty");
        }
        return accountDao.findByUnameAndPwd(uname, pwd);
    }

    @Override
    public List<Account> getAll() {
        return  accountDao.findAll();
    }
}
