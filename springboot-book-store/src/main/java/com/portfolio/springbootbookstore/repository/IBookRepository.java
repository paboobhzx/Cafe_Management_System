package com.portfolio.springbootbookstore.repository;

import com.portfolio.springbootbookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookRepository extends JpaRepository<Book, Long> {
}
