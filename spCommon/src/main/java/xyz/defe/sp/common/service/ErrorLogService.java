package xyz.defe.sp.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.dao.ErrorLogDao;
import xyz.defe.sp.common.entity.general.ErrorLog;

import java.util.Map;

@Service
public class ErrorLogService {
    @Autowired
    private ErrorLogDao errorLogDao;
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

    public void log(String operation, String error, Map data) {
        ErrorLog log = new ErrorLog();
        log.setOperation(operation);
        log.setError(error);
        log.setDataJson(gson.toJson(data));
        errorLogDao.saveAndFlush(log);
    }
}
