package com.inn.cafe.JWT;

import com.inn.cafe.dao.UserDao;
import com.inn.cafe.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
@Service
@Slf4j

public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private User userDetail; //com.inn.cafe.model.User
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUserName  CustomerUserDetailsService.java{}", username);
        userDetail = userDao.findByEmailId(username);
        if(!Objects.isNull(userDetail)){
            String pass = userDetail.getPassword();
            String usr = userDetail.getEmail();
            return new org.springframework.security.core.userdetails.User(userDetail.getEmail(),
                    userDetail.getPassword(),
                    true,
                    true,
                    true,
                    true,
                    new ArrayList<>());
        }
        else
            throw new UsernameNotFoundException("User not found");
    }

    public User getUserDetail() {


        return userDetail;
    }


}
