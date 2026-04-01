package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // Since you only have one main portfolio, we might just fetch by ID 1
}