package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByEmailContainingIgnoreCaseOrReasonContainingIgnoreCaseOrderByCreatedAtDesc(
            String email,
            String reason
    );
}
