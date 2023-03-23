package com.portfolio.springbootbookstore.repository;

import com.portfolio.springbootbookstore.model.Role;
import com.portfolio.springbootbookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String userName);

    @Modifying
    @Query("update User set role = :role where username = :username")
    void updateUserRole(@Param("username") String userName, @Param("role")Role role);
}
