package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.Headline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeadlineRepository extends JpaRepository<Headline, Long> {
    List<Headline> findByProfileIdOrderByIdAsc(Long profileId);
}
