package com.inn.cafe.utils;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

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
}
