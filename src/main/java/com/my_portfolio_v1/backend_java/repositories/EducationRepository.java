package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    @Query("""
        SELECT e FROM Education e
        WHERE e.profile.id = :profileId
        ORDER BY
            CASE WHEN e.toDate IS NULL THEN 0 ELSE 1 END,
            e.toDate DESC,
            e.fromDate DESC
    """)
    List<Education> findAllByProfileIdOrdered(Long profileId);
}
