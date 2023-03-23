package com.portfolio.springbootbookstore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "purchase_history")
public class PurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userID;

    @Column(name = "book_id", nullable=false )
    private Long BookId;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "purchase_time", nullable = false)
    private LocalDateTime purchaseTime;
}
