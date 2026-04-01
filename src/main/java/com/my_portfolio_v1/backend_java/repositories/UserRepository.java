package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query method to find a user by email
    Optional<User> findByEmail(String email);
}