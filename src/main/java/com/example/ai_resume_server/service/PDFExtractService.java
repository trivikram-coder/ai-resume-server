package com.example.ai_resume_server.service;



import com.example.ai_resume_server.DTO.Prompt;

import com.example.ai_resume_server.models.ResumeReport;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;




@Service
public class PDFExtractService {
    @Autowired
    private ResumeReportService resumeReportService;

    public Prompt extractText(MultipartFile file, String description) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {

            // Extract PDF text
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // AI Prompt Message
            Prompt pmp=new Prompt();

            String rules =
                    "You are an ATS resume analyzer. " +
                            "The job description is: " + description + ". " +
                            "When I provide resume text, you must respond ONLY in the following fixed sentence format: " +
                            "ATS_SCORE: <score>; STRENGTHS: <comma separated strengths>; IMPROVEMENTS: <comma separated improvements>; JOB_MATCH: <score>;JOB_RECOMMENDATION:<comma separated jobs>; MISSING_KEYWORDS: <comma separated keywords>; SUMMARY: <one sentence final summary>. " +
                            "Strict rules: " +
                            "- Do NOT use JSON. " +
                            "- Do NOT use bullet points. " +
                            "- Do NOT add extra text. " +
                            "- Respond in ONE single line. " +
                            "- Do NOT include new lines or formatting. " +
                            "- Do NOT change keyword labels (ATS_SCORE, STRENGTHS, IMPROVEMENTS, JOB_MATCH, MISSING_KEYWORDS, SUMMARY). " +
                            "- Replace only the values. " +
                            "Example: ATS_SCORE: 70; STRENGTHS: good Java skills, strong projects; IMPROVEMENTS: add measurable achievements; JOB_MATCH: 65;JOB_RECOMMENDATION:Java Developer,Backend Developer; MISSING_KEYWORDS: spring boot, rest api; SUMMARY: candidate has solid fundamentals but needs more project depth. ";

            // Final prompt = rules + extracted resume text

            pmp.setPrompt(rules + " Resume Text: " + text);
            // Send to AI service
            return pmp;

        } catch (Exception e) {
           e.printStackTrace();

            return new Prompt();
        }
    }
}
