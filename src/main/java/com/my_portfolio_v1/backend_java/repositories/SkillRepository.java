package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
}