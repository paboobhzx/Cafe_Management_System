package com.inn.cafe.wrapper;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor


public class UserWrapper {
    //UserWrapper user = new UserWrapper();
    private Integer id;
    private String name;
    private String email;

    private String contactNumber;
    private String status;

    public UserWrapper(Integer id, String name, String email, String contactNumber, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.status = status;
    }

}
