package com.smarthospital.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class TriageDtos {
    public static class TriageRequest {
        @NotBlank private String symptoms;
        public String getSymptoms() { return symptoms; }
        public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    }

    public static class TriageResult {
        private String urgency;            // LOW, MEDIUM, HIGH, EMERGENCY
        private String specialization;
        private String possibleCondition;
        private String advice;
        private int confidence;            // 0-100
        private List<String> matchedSymptoms;

        public TriageResult(String urgency, String specialization, String possibleCondition,
                            String advice, int confidence, List<String> matchedSymptoms) {
            this.urgency = urgency; this.specialization = specialization;
            this.possibleCondition = possibleCondition; this.advice = advice;
            this.confidence = confidence; this.matchedSymptoms = matchedSymptoms;
        }
        public String getUrgency() { return urgency; }
        public String getSpecialization() { return specialization; }
        public String getPossibleCondition() { return possibleCondition; }
        public String getAdvice() { return advice; }
        public int getConfidence() { return confidence; }
        public List<String> getMatchedSymptoms() { return matchedSymptoms; }
    }
}
