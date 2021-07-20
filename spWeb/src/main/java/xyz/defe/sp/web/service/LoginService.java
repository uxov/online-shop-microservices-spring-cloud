package xyz.defe.sp.web.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.RestUtil;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;

@Service
public class LoginService extends BaseService {
    public Account login(String uname, String pwd) throws Exception {
        ResponseData<Account> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .post(baseURL + "userService/verify?uname={uname}&pwd={pwd}",
                            new ParameterizedTypeReference<ResponseData<Account>>() {}, uname, pwd);
        });
        return responseData.getData();
    }
}
