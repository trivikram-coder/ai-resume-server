package com.example.ai_resume_server.repo;

import com.example.ai_resume_server.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account,Long> {
    boolean existsByEmail(String email);
    boolean deleteByEmail(String email);
    Account findByEmail(String email);
}
