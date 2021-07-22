package xyz.defe.sp.common.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.util.Strings;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.ArrayList;
import java.util.List;

public class ResponseDataUtil {
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

    public static ResponseData getData(String responseDataJson, Class type) {
        if (Strings.isBlank(responseDataJson)) {return new ResponseData();}
        ResponseData responseData = gson.fromJson(responseDataJson,
                TypeToken.getParameterized(ResponseData.class, type).getType());
        return responseData;
    }

    public static <T> List<T> getDataAsList(ResponseData responseData, Class<T> type) {
        Object data = responseData.getData();
        if (data == null) {return new ArrayList<>();}
        String json = gson.toJson(data);
        List<T> list = gson.fromJson(json, TypeToken.getParameterized(ArrayList.class, type).getType());
        return list;
    }

}
