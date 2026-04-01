package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {
    // JpaRepository gives us findAll(), findById(), save(), etc., for free!
}
