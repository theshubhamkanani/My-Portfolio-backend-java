package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    // Standard CRUD operations are automatically available
}