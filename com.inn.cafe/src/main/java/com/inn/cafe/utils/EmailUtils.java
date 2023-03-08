package com.inn.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        for(int i=0; i < ccList.size(); i++)
            cc[i] = ccList.get(i);

        return cc;
    }

    public void forgotMail(String to, String subject, String Password )throws MessagingException{
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("zorakbug@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlMsg = "<p><b>Your Login Details for Cafe Management System</b><br><b>" +
                "Email: </b> " + to + "<br><b>Passowrd: </b>" + Password + "<br><a href=\"\"http://localhost:4200/\">Click here to login</a></p>";
        message.setContent(htmlMsg, "text/html");
        emailSender.send(message);


    }



}
