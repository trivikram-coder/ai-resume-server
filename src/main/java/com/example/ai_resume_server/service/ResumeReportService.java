package com.example.ai_resume_server.service;

import com.example.ai_resume_server.DTO.ApiResponse;
import com.example.ai_resume_server.DTO.Prompt;
import com.example.ai_resume_server.models.ResumeReport;
import com.example.ai_resume_server.repo.AccountRepo;
import com.example.ai_resume_server.repo.ResumeReportRepo;
import com.example.ai_resume_server.utill.ResumeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeReportService {

    @Autowired
    private ResumeReportRepo repo;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private ResumeUtil resumeUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${API_URL}")
    private String url;

    /**
     * Calls Gemini API safely with retry + exponential backoff
     */
    public ResumeReport sendPrompt(Prompt prompt) {

        int maxRetries = 3;
        long waitTime = 2000; // 2 seconds

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ApiResponse response =
                        restTemplate.postForObject(url, prompt, ApiResponse.class);

                if (response == null || response.getResponse() == null) {
                    System.out.println("⚠️ Empty Gemini response");
                    return new ResumeReport();
                }

                return resumeUtil.extractObj(response.getResponse());

            } catch (HttpClientErrorException e) {

                // Handle Gemini rate limit
                if (e.getStatusCode().value() == 429) {
                    System.out.println(
                            "⚠️ Gemini 429 rate limit. Retry "
                                    + attempt + "/" + maxRetries);

                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ignored) {}

                    waitTime *= 2; // exponential backoff
                } else {
                    e.printStackTrace();
                    break; // non-429 error → stop retry
                }

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        System.out.println("❌ Gemini API failed after retries");
        return new ResumeReport(); // graceful fallback
    }

    /**
     * Get cached reports for an email
     */
    @Cacheable(cacheNames = "resumeReport", key = "#email")
    public List<ResumeReport> getReport(String email) {

        Optional<List<ResumeReport>> optional =
                repo.findByEmailOrderByIdDesc(email);

        return optional.orElse(Collections.emptyList());
    }

    /**
     * Delete report and evict cache
     */
    public boolean delete(Long id) {

        Optional<ResumeReport> optional = repo.findById(id);

        if (optional.isEmpty()) {
            return false;
        }

        String email = optional.get().getEmail();

        repo.deleteById(id);

        if (cacheManager.getCache("resumeReport") != null) {
            cacheManager.getCache("resumeReport").evict(email);
        }

        return true;
    }
}
