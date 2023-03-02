package com.inn.cafe.model;

import com.inn.cafe.wrapper.UserWrapper;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "User.findByEmailId", query = "select u from User u where u.email=:email")
@NamedQuery(name = "User.getAllUser", query = "select new com.inn.cafe.wrapper.UserWrapper(u.id, u.name, u.email, u.contactNumber, u.status) from User u where u.role = 'user'")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
@Data //LOMBOK

//Integer id, String name, String email, String contactNumber, String status
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    //UserWrapper usr = new UserWrapper(1,"abc","abc@gmail.com","558877", "false");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "contactNumber")
    private String contactNumber;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "status")
    private String status;
    @Column(name = "role")
    private String role;

}
