package com.smarthospital.controller;

import com.smarthospital.dto.TriageDtos.TriageRequest;
import com.smarthospital.dto.TriageDtos.TriageResult;
import com.smarthospital.service.TriageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final TriageService triageService;

    public AiController(TriageService triageService) { this.triageService = triageService; }

    @PostMapping("/triage")
    public TriageResult triage(@Valid @RequestBody TriageRequest req) {
        return triageService.analyze(req.getSymptoms());
    }
}
