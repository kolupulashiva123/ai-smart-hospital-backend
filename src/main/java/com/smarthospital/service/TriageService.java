package com.smarthospital.service;

import com.smarthospital.dto.TriageDtos.TriageResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Rule-based AI triage engine.
 * Scores the patient's free-text symptoms against a knowledge base of conditions,
 * then suggests a specialization, urgency level and first-aid advice.
 * (Educational tool - not a medical diagnosis.)
 */
@Service
public class TriageService {

    private record Rule(String condition, String specialization, String urgency,
                        List<String> keywords, String advice) {}

    private static final List<String> EMERGENCY_KEYWORDS = List.of(
            "chest pain", "can't breathe", "cannot breathe", "difficulty breathing", "shortness of breath",
            "unconscious", "fainted", "seizure", "stroke", "face drooping", "slurred speech",
            "severe bleeding", "heavy bleeding", "vomiting blood", "suicidal", "poison", "overdose",
            "heart attack", "paralysis");

    private static final List<Rule> RULES = List.of(
        new Rule("Heart-related condition", "Cardiologist", "HIGH",
            List.of("chest", "palpitation", "heart", "racing heart", "chest tightness", "left arm", "high blood pressure", "bp", "swelling in legs"),
            "Avoid exertion, sit upright and stay calm. Get an ECG and blood pressure check soon."),
        new Rule("Neurological issue (e.g. migraine)", "Neurologist", "MEDIUM",
            List.of("migraine", "severe headache", "dizziness", "vertigo", "numbness", "tingling", "memory loss", "tremor", "blurred vision", "headache"),
            "Rest in a dark, quiet room, stay hydrated and avoid screens. Seek care if symptoms worsen."),
        new Rule("Skin condition", "Dermatologist", "LOW",
            List.of("rash", "itching", "itch", "acne", "pimples", "skin", "eczema", "hair fall", "hives", "allergy", "redness", "fungal"),
            "Avoid scratching, keep the area clean and dry, and avoid new cosmetics until reviewed."),
        new Rule("Bone / joint problem", "Orthopedic", "MEDIUM",
            List.of("fracture", "bone", "joint pain", "knee", "back pain", "shoulder", "sprain", "swollen joint", "neck pain", "ankle", "hip pain"),
            "Rest the affected area, apply a cold pack for 15 minutes and avoid heavy lifting."),
        new Rule("Childhood illness", "Pediatrician", "MEDIUM",
            List.of("child", "baby", "infant", "toddler", "my son", "my daughter", "vaccination", "kid"),
            "Keep the child hydrated and monitor temperature. Bring vaccination records to the visit."),
        new Rule("Ear, nose or throat infection", "ENT", "LOW",
            List.of("ear pain", "earache", "sore throat", "throat", "sinus", "blocked nose", "runny nose", "hearing", "tonsil", "ear", "nose bleed"),
            "Drink warm fluids, gargle with warm salt water and avoid cold drinks."),
        new Rule("Women's health concern", "Gynecologist", "MEDIUM",
            List.of("pregnan", "period", "menstrual", "pcos", "pelvic", "vaginal", "irregular cycle", "breast"),
            "Track symptoms and dates of your cycle and bring them to the consultation."),
        new Rule("Digestive problem", "General Physician", "LOW",
            List.of("stomach", "abdominal", "acidity", "gas", "diarrhea", "vomiting", "nausea", "constipation", "indigestion", "loose motion"),
            "Stay hydrated with ORS, eat light food and avoid oily or spicy meals."),
        new Rule("Viral infection / flu", "General Physician", "LOW",
            List.of("fever", "cold", "cough", "body ache", "chills", "fatigue", "tired", "weakness", "flu", "viral"),
            "Rest, drink plenty of fluids and monitor your temperature. See a doctor if fever lasts more than 3 days."),
        new Rule("Diabetes-related symptoms", "General Physician", "MEDIUM",
            List.of("diabetes", "sugar", "frequent urination", "excessive thirst", "thirsty", "weight loss"),
            "Get a fasting and post-meal blood sugar test and avoid sugary foods until reviewed.")
    );

    public TriageResult analyze(String symptoms) {
        String text = symptoms.toLowerCase(Locale.ROOT);

        // 1. Emergency check
        List<String> emergencyHits = new ArrayList<>();
        for (String k : EMERGENCY_KEYWORDS) {
            if (text.contains(k)) emergencyHits.add(k);
        }
        if (!emergencyHits.isEmpty()) {
            return new TriageResult("EMERGENCY", "Cardiologist",
                    "Possible medical emergency",
                    "These symptoms can be serious. Go to the nearest emergency room or call 108 / 112 immediately. Do not wait for an appointment.",
                    95, emergencyHits);
        }

        // 2. Score each rule
        Rule best = null;
        List<String> bestHits = new ArrayList<>();
        for (Rule r : RULES) {
            List<String> hits = new ArrayList<>();
            for (String k : r.keywords()) {
                if (text.contains(k)) hits.add(k);
            }
            if (hits.size() > bestHits.size()) {
                best = r;
                bestHits = hits;
            }
        }

        if (best == null) {
            return new TriageResult("LOW", "General Physician", "Unclear - needs examination",
                    "We could not match your description to a specific condition. A general physician can examine you and refer you if needed.",
                    30, List.of());
        }

        int confidence = Math.min(90, 45 + bestHits.size() * 15);
        String urgency = best.urgency();
        // Severity words bump the urgency
        if (text.contains("severe") || text.contains("unbearable") || text.contains("worst")) {
            urgency = urgency.equals("LOW") ? "MEDIUM" : "HIGH";
        }
        return new TriageResult(urgency, best.specialization(), best.condition(),
                best.advice(), confidence, bestHits);
    }
}
