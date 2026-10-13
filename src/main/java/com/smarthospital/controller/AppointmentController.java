package com.smarthospital.controller;

import com.smarthospital.dto.AppointmentDtos.BookingRequest;
import com.smarthospital.dto.AppointmentDtos.StatusUpdate;
import com.smarthospital.exception.BadRequestException;
import com.smarthospital.exception.ResourceNotFoundException;
import com.smarthospital.model.*;
import com.smarthospital.repository.AppointmentRepository;
import com.smarthospital.repository.DoctorRepository;
import com.smarthospital.repository.PatientRepository;
import com.smarthospital.service.TriageService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    public static final List<String> SLOTS = List.of(
            "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
            "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM");

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final TriageService triageService;

    public AppointmentController(AppointmentRepository appointmentRepo, DoctorRepository doctorRepo,
                                 PatientRepository patientRepo, TriageService triageService) {
        this.appointmentRepo = appointmentRepo; this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo; this.triageService = triageService;
    }

    /** Public: free slots for a doctor on a date */
    @GetMapping("/slots")
    public List<String> slots(@RequestParam Long doctorId,
                              @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        List<String> free = new ArrayList<>(SLOTS);
        appointmentRepo.findByDoctorIdAndAppointmentDateAndStatusNot(doctorId, date, AppointmentStatus.CANCELLED)
                .forEach(a -> free.remove(a.getTimeSlot()));
        return free;
    }

    /** Public: patient books without any login - phone number is their identity */
    @PostMapping("/book")
    public Appointment book(@Valid @RequestBody BookingRequest req) {
        if (req.getDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Appointment date cannot be in the past");
        }
        if (!SLOTS.contains(req.getTimeSlot())) {
            throw new BadRequestException("Invalid time slot");
        }
        Doctor doctor = doctorRepo.findById(req.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        if (!Boolean.TRUE.equals(doctor.getAvailable())) {
            throw new BadRequestException("This doctor is currently not available");
        }
        boolean taken = appointmentRepo
                .findByDoctorIdAndAppointmentDateAndStatusNot(doctor.getId(), req.getDate(), AppointmentStatus.CANCELLED)
                .stream().anyMatch(a -> a.getTimeSlot().equals(req.getTimeSlot()));
        if (taken) throw new BadRequestException("That slot is already booked. Please choose another.");

        Patient patient = patientRepo.findByPhone(req.getPhone()).orElseGet(Patient::new);
        patient.setName(req.getPatientName());
        patient.setAge(req.getAge());
        patient.setGender(req.getGender());
        patient.setPhone(req.getPhone());
        patient = patientRepo.save(patient);

        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setAppointmentDate(req.getDate());
        a.setTimeSlot(req.getTimeSlot());
        a.setIssue(req.getIssue());
        a.setUrgency(triageService.analyze(req.getIssue()).getUrgency());
        a.setFee(doctor.getFee());
        a.setStatus(AppointmentStatus.PENDING);
        return appointmentRepo.save(a);
    }

    /** Public: patient tracks appointments by phone number */
    @GetMapping("/track")
    public List<Appointment> track(@RequestParam String phone) {
        return appointmentRepo.findByPatientPhoneOrderByAppointmentDateDescIdDesc(phone);
    }

    /** Staff: admin sees all, doctor sees own */
    @GetMapping
    public List<Appointment> list(Authentication auth) {
        if (isAdmin(auth)) return appointmentRepo.findAllByOrderByAppointmentDateDescIdDesc();
        Doctor d = currentDoctor(auth);
        return appointmentRepo.findByDoctorIdOrderByAppointmentDateDescIdDesc(d.getId());
    }

    @PutMapping("/{id}/status")
    public Appointment updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdate req, Authentication auth) {
        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (!isAdmin(auth) && !a.getDoctor().getId().equals(currentDoctor(auth).getId())) {
            throw new BadRequestException("You can only update your own appointments");
        }
        AppointmentStatus status;
        try {
            status = AppointmentStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + req.getStatus());
        }
        a.setStatus(status);
        if (req.getNotes() != null && !req.getNotes().isBlank()) a.setDoctorNotes(req.getNotes());
        return appointmentRepo.save(a);
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
    }

    private Doctor currentDoctor(Authentication auth) {
        return doctorRepo.findByUserUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
    }
}
