package com.inn.cafe.model;

import com.google.common.base.Strings;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Product.getAllProducts", query = "select new com.inn.cafe.wrapper.ProductWrapper(p.id, p.name, p.description, p.price, p.status, p.category.id, p.category.name) from Product p")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "product")
@Data //LOMBOK
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private Integer price;
    @Column(name = "status")
    private String status;

    public boolean isValid(){
        if(!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(description)){
            if(price > 0){
                return true;
            }
        }
        return false;
    }

}
