package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.CustomerUserDetailsService;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtil;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.model.User;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    EmailUtils emailUtils;
    @Autowired
    UserDao userDao;
    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try
        {
            log.info("Inside signup {}", requestMap);
            if(validateSignUpMap(requestMap)){
                User user = userDao.findByEmailId(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully registered", HttpStatus.OK);
                }
                else
                {
                    return CafeUtils.getResponseEntity("Emails already exists", HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private boolean validateSignUpMap(Map<String,String> requestMap){
        if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")){
            return true;
        }
        return false;
    }
    private String BEncryptPassword(String password){
        String encryptedPass = new BCryptPasswordEncoder().encode(password);
        return encryptedPass;
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        var encryptedPass = new BCryptPasswordEncoder().encode(requestMap.get("password"));
        user.setPassword(encryptedPass);
        user.setStatus("false");
        user.setRole("user");
        return user;
    }
    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login UserServiceImpl.java");
        try
        {

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),
                            requestMap.get("password"))
            );
            if(auth.isAuthenticated()){
                log.info("Inside auth.isauthenticated UserServiceImpl.java");
                if(customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    var tmpResponse = new ResponseEntity<String>("{\"token\":\""+
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole())
                            + "\"}", HttpStatus.OK);

                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole())
                            + "\"}", HttpStatus.OK);
                }
            }
            else
            {
                return new ResponseEntity<String>
                        ("{\"message\":\"" + "Wait for admin approval " + "\"}",HttpStatus.BAD_REQUEST );
            }
        }
        catch(Exception ex)
        {
            log.error("{}",ex);
        }
        return new ResponseEntity<String>
                ("{\"message\":\"" + "Bad Credentials " + "\"}",HttpStatus.BAD_REQUEST );
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try
        {
            if(jwtFilter.isAdmin())
            {
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try
        {
            if(jwtFilter.isAdmin()){
                Optional<User> findUser = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!findUser.isEmpty()){
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), findUser.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);

                }
                else {
                    return CafeUtils.getResponseEntity("User ID doesn't exist", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        String currUser = jwtFilter.getCurrentUser();
        if(status != null && status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(currUser, "Account Approved by " + "\n" + "ADMIN: " + currUser, "The account was approved", allAdmin);
        }
        else {
            emailUtils.sendSimpleMessage(currUser, "Account Disabled by " + "\n" + "ADMIN: " + currUser, "The account was disabled", allAdmin);

        }
    }
    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try
        {

            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if(!userObj.equals(null)){
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                var matchingPassword = encoder.matches(requestMap.get("oldPassword"), userObj.getPassword());
                if(matchingPassword) //password matches. decrypted from db and compared to the new one
                {
                    sendMailToAllAdmin(userObj.getStatus(), userObj.getEmail(), userDao.getAllAdmin());
                    String encryptedPass = encoder.encode(requestMap.get("newPassword"));
                    userObj.setPassword(encryptedPass);
                    userDao.save(userObj);
                    return CafeUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
                }

                return CafeUtils.getResponseEntity("Incorrect old password", HttpStatus.BAD_REQUEST);

            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try
        {
            User userObj = userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(userObj) && !Strings.isNullOrEmpty(userObj.getEmail()))
                emailUtils.forgotMail(userObj.getEmail(), "Credentials by Cafe Managament", userObj.getPassword());
            return CafeUtils.getResponseEntity("Check your mail for Credentials", HttpStatus.OK);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
