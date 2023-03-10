package com.inn.cafe.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "Bill")
@Data //LOMBOK


//@NamedQuery(name = "BIll.getBillByUserName", query = "select b from Bill b where b.createdBy=:username order by b.id desc")
//@NamedQuery(name = "Product.getProductById", query = "select new com.inn.cafe.wrapper.ProductWrapper(p.id, p.name, p.description, p.price) from Product p where p.id =: id")

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "contactNumber")
    private String contactNumber;
    @Column(name = "paymentMethod")
    private String paymentMethod;
    @Column(name = "total")
    private Integer total;
    @Column(name = "productDetail", columnDefinition = "json")
    private String productDetail;
    @Column(name = "createdBy")
    private String createdBy;
}
