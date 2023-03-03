package com.inn.cafe.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> destList){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("zorakbug@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if(destList != null && destList.size() > 0)
        {
            message.setCc(getCcArray(destList));
        }
        emailSender.send(message);
    }
    private String[] getCcArray(List<String> ccList){
        String[] cc = new String[ccList.size()];
        for(int i=0; i < cc.length; i++)
            cc[i] = ccList.get(i);

        return cc;

    }

}
