package com.beauty.cart.repository;

import com.beauty.cart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user From User user WHERE user.email = :email")
    Optional<User> findUserByEmail(String email);
}
