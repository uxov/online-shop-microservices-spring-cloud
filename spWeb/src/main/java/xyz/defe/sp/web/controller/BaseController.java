package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import xyz.defe.sp.common.ResponseWrap;
import xyz.defe.sp.common.pojo.ResponseData;

public class BaseController {
    @Autowired
    private ResponseWrap responseWrap;

    public ResponseData response(ResponseWrap.ThrowingRunnable func) {
        return responseWrap.wrap(func);
    }
}
