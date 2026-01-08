package com.example.ai_resume_server.utill;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.ai_resume_server.DTO.ApiResponse;
import com.example.ai_resume_server.models.ResumeReport;
import com.example.ai_resume_server.repo.ResumeReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class ResumeUtil {
    @Autowired
    private ResumeReportRepo repo;

    public ResumeReport extractObj(String data) {



        String regex =
                "ATS_SCORE:\\s*([^;]+);\\s*" +
                        "STRENGTHS:\\s*([^;]+);\\s*" +
                        "IMPROVEMENTS:\\s*([^;]+);\\s*" +
                        "JOB_MATCH:\\s*([^;]+);\\s*" +
                        "JOB_RECOMMENDATION:\\s*([^;]+);\\s*" +
                        "MISSING_KEYWORDS:\\s*([^;]+);\\s*" +
                        "SUMMARY:\\s*(.*)";


        Matcher m = Pattern.compile(regex).matcher(data);
        ResumeReport response=new ResumeReport();

        if (!m.find()) {

            response.setError("Pattern mismatch");
            System.out.println("Pattern mismatch");
            response.setMessage(data);
            return response;
        }

        response.setAtsScore(Long.parseLong(m.group(1).trim()));
        response.setStrengths(List.of(m.group(2).trim().split(",")));
        response.setImprovements(List.of(m.group(3).trim().split(",")));
        response.setJobMatch(Long.parseLong(m.group(4).trim()));
        response.setJobRecommendation(List.of(m.group(5).trim().split(",")));
        response.setMissingKeywords(List.of(m.group(6).trim().split(",")));
        response.setSummary(m.group(7).trim());

        return response;
    }

}
