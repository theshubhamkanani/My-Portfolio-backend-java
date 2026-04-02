package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {
    List<SkillCategory> findByProfileIdOrderByNameAsc(Long profileId);
    Optional<SkillCategory> findByProfileIdAndNameIgnoreCase(Long profileId, String name);
    Optional<SkillCategory> findByProfileIdAndNameIgnoreCaseAndIdNot(Long profileId, String name, Long id);
}
