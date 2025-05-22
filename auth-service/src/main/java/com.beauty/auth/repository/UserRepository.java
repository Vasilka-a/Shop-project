package com.beauty.auth.repository;


import com.beauty.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT DISTINCT user FROM User user LEFT JOIN FETCH user.rolesSet WHERE user.email = :email")
    Optional<User> findByEmail(String email);
}
