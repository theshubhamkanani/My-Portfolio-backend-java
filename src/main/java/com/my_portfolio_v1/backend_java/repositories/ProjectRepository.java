package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Sorts by Current Projects first, then by Start Date descending
    @Query("SELECT p FROM Project p WHERE p.profile.id = :profileId " +
            "ORDER BY p.isCurrentProject DESC, p.startDate DESC")
    List<Project> findAllByProfileIdSorted(Long profileId);
}