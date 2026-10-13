package com.smarthospital.controller;

import com.smarthospital.exception.ResourceNotFoundException;
import com.smarthospital.model.Appointment;
import com.smarthospital.model.Patient;
import com.smarthospital.repository.AppointmentRepository;
import com.smarthospital.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;

    public PatientController(PatientRepository patientRepo, AppointmentRepository appointmentRepo) {
        this.patientRepo = patientRepo; this.appointmentRepo = appointmentRepo;
    }

    @GetMapping
    public List<Patient> all() { return patientRepo.findAll(); }

    @GetMapping("/{id}")
    public Patient one(@PathVariable Long id) {
        return patientRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    @GetMapping("/{id}/history")
    public List<Appointment> history(@PathVariable Long id) {
        Patient p = one(id);
        return appointmentRepo.findByPatientPhoneOrderByAppointmentDateDescIdDesc(p.getPhone());
    }
}
