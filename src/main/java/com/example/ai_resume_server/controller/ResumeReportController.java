package com.example.ai_resume_server.controller;

import com.example.ai_resume_server.DTO.Prompt;
import com.example.ai_resume_server.models.ResumeReport;
import com.example.ai_resume_server.repo.AccountRepo;
import com.example.ai_resume_server.repo.ResumeReportRepo;
import com.example.ai_resume_server.service.PDFExtractService;
import com.example.ai_resume_server.service.PDFValidatorService;
import com.example.ai_resume_server.service.ResumeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/resume")
public class ResumeReportController {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PDFValidatorService validatorService;

    @Autowired
    private ResumeReportRepo reportRepo;

    @Autowired
    private ResumeReportService resumeService;

    @Autowired
    private PDFExtractService pdfExtractService;


    // -------------------- Upload Resume --------------------
    @PostMapping("/upload/{email}")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            @PathVariable String email) {

        if (!accountRepo.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", false, "message", "Email not registered."));
        }
        if(description.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("status",false,"message","Please provide job description"));
        }
        String validationResult = validatorService.validate(file);
        if (!validationResult.equals("VALID")) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", false, "message", validationResult)
            );
        }

        // Extract → send prompt → generate report
        Prompt prompt = pdfExtractService.extractText(file, description);
        ResumeReport report=resumeService.sendPrompt(prompt);
        if (report == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", false, "message", "Unable to generate resume report.")
            );
        }

        // SET EMAIL HERE (the ONLY correct place)
        report.setEmail(email);

        // SAVE REPORT
        reportRepo.save(report);

        // NOW CLEAR CACHE
        if (cacheManager.getCache("resumeReport") != null) {
            cacheManager.getCache("resumeReport").evict(email);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("status", true, "message", "Resume report generated successfully", "reportId", report.getId())
        );
    }



    // -------------------- Get Resume Reports --------------------
    @GetMapping("/report/{email}")
    public ResponseEntity<?> getResumeReport(@PathVariable String email) {

        if (email.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", false,
                            "message", "Email is required"
                    ));
        }

        if (!accountRepo.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", false,
                            "message", "No account found with this email"
                    ));
        }

        Optional<List<ResumeReport>> reports = Optional.ofNullable(resumeService.getReport(email));

        if (reports.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", false,
                            "message", "No resume reports found for this email"
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "status", true,
                        "message", "Reports fetched successfully",
                        "reports", reports
                )
        );
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id){
        if(!reportRepo.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status",false,"message","No report exists with this id"));
        }
        boolean delete=resumeService.delete(id);
        if(!delete){
            return ResponseEntity.badRequest().body("");
        }
        return ResponseEntity.ok().body(Map.of("status",true,"message","Report deleted successfully"));
    }
}
