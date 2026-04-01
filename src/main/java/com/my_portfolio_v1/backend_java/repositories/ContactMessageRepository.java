package com.my_portfolio_v1.backend_java.repositories;

import com.my_portfolio_v1.backend_java.models.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    // Standard CRUD operations are ready to go
}
