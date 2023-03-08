package com.inn.cafe.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Category.getAllCategory", query = "select c from Category c where c.id in (select p.category from Product p where p.status  = 'true')")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "category")
@Data //LOMBOK
public class Category implements Serializable  {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
}
