package com.example.ai_resume_server.DTO;

import lombok.Data;

@Data
public class ApiResponse {
    private String response;

    public void setError(String patternMismatch) {
    }

    public void setMessage(String data) {

    }
}
