package com.inn.cafe.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CafeUtils {

    private CafeUtils(){


    }
    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}", httpStatus);
    }

    public static String getUUID(){
        Date specDate = new Date();
        long time = specDate.getTime();
        return "BILL-" + time;
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException{
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data){
        if(!Strings.isNullOrEmpty(data)){
            return new Gson().fromJson(data , new TypeToken<Map<String,Object>>()
            {
            }.getType());
        }
        return new HashMap<>();
    }

    public static Boolean fileExists(String filePath){
        log.info("Inside fileExists {}", filePath);
        try
        {
            File fileObj = new File(filePath);
            return (fileObj != null && fileObj.exists()) ? Boolean.TRUE : Boolean.FALSE;

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

}
