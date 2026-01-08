package com.example.ai_resume_server.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "resume_report")
public class ResumeReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long atsScore;

    @ElementCollection
    private List<String> strengths;

    @ElementCollection
    private List<String> improvements;

    private Long jobMatch;

    @ElementCollection
    private List<String> jobRecommendation;

    @ElementCollection
    private List<String> missingKeywords;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String email;

    public void setMessage(String data) {
        // implement only if needed
    }

    public void setError(String patternMismatch) {
        // implement only if needed
    }
}
