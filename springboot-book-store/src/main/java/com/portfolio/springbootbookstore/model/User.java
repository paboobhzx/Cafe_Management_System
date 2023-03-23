package com.portfolio.springbootbookstore.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity //JPA
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "passWord",  nullable = false, length = 100)
    private String passWord;

    @Column(name = "creationTime", nullable = false)
    private String creationTime;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String userName;


}
