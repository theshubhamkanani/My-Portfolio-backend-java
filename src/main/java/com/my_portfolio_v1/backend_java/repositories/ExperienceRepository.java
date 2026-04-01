package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    // 1. Find all experiences for your profile, sorted by date
    // We want: isCurrentJob (True first), then startDate (Descending)
    @Query("SELECT e FROM Experience e WHERE e.profile.id = :profileId " +
            "ORDER BY e.isCurrentJob DESC, e.startDate DESC")
    List<Experience> findAllByProfileIdSorted(Long profileId);
}