package com.example.ai_resume_server.repo;
import java.util.*;
import com.example.ai_resume_server.models.ResumeReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeReportRepo extends JpaRepository<ResumeReport,Long> {
    Optional<List<ResumeReport>> findByEmailOrderByIdDesc(String email);
    boolean existsById(Long id);
}
