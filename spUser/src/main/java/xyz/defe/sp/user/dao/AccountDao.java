package xyz.defe.sp.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.defe.sp.common.entity.spUser.Account;

@Repository
public interface AccountDao extends JpaRepository<Account, String> {
    Account findByUnameAndPwd(String uname, String pwd);
}
