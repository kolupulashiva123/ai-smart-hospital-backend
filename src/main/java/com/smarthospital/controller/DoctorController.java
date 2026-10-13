package com.smarthospital.controller;

import com.smarthospital.dto.AppointmentDtos.DoctorRequest;
import com.smarthospital.exception.BadRequestException;
import com.smarthospital.exception.ResourceNotFoundException;
import com.smarthospital.model.Doctor;
import com.smarthospital.model.Role;
import com.smarthospital.model.User;
import com.smarthospital.repository.AppointmentRepository;
import com.smarthospital.repository.DoctorRepository;
import com.smarthospital.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepo;
    private final UserRepository userRepo;
    private final AppointmentRepository appointmentRepo;
    private final PasswordEncoder encoder;

    public DoctorController(DoctorRepository doctorRepo, UserRepository userRepo,
                            AppointmentRepository appointmentRepo, PasswordEncoder encoder) {
        this.doctorRepo = doctorRepo; this.userRepo = userRepo;
        this.appointmentRepo = appointmentRepo; this.encoder = encoder;
    }

    @GetMapping
    public List<Doctor> list(@RequestParam(required = false) String specialization) {
        if (specialization != null && !specialization.isBlank()) {
            return doctorRepo.findBySpecializationIgnoreCase(specialization);
        }
        return doctorRepo.findAll();
    }

    @GetMapping("/{id}")
    public Doctor get(@PathVariable Long id) {
        return doctorRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    @PostMapping
    public Doctor create(@Valid @RequestBody DoctorRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        User u = new User(req.getUsername(), encoder.encode(req.getPassword()), req.getName(), Role.DOCTOR);
        Doctor d = new Doctor();
        d.setName(req.getName());
        d.setSpecialization(req.getSpecialization());
        d.setFee(req.getFee());
        d.setExperienceYears(req.getExperienceYears());
        d.setAvailable(true);
        d.setUser(u);
        return doctorRepo.save(d);
    }

    @PutMapping("/{id}/availability")
    public Doctor toggleAvailability(@PathVariable Long id) {
        Doctor d = get(id);
        d.setAvailable(!Boolean.TRUE.equals(d.getAvailable()));
        return doctorRepo.save(d);
    }

    @PutMapping("/{id}/fee")
    public Doctor updateFee(@PathVariable Long id, @RequestBody Map<String, Double> body) {
        Double fee = body.get("fee");
        if (fee == null || fee <= 0) throw new BadRequestException("Fee must be positive");
        Doctor d = get(id);
        d.setFee(fee);
        return doctorRepo.save(d);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        Doctor d = get(id);
        if (appointmentRepo.countByDoctorId(id) > 0) {
            throw new BadRequestException("This doctor has appointments. Mark the doctor unavailable instead of deleting.");
        }
        doctorRepo.delete(d);
        return Map.of("message", "Doctor deleted");
    }
}
