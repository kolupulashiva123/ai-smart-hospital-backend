package com.smarthospital.controller;

import com.smarthospital.dto.DashboardStats;
import com.smarthospital.exception.ResourceNotFoundException;
import com.smarthospital.model.AppointmentStatus;
import com.smarthospital.model.Doctor;
import com.smarthospital.repository.AppointmentRepository;
import com.smarthospital.repository.DoctorRepository;
import com.smarthospital.repository.PatientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;

    public DashboardController(AppointmentRepository appointmentRepo, DoctorRepository doctorRepo,
                               PatientRepository patientRepo) {
        this.appointmentRepo = appointmentRepo; this.doctorRepo = doctorRepo; this.patientRepo = patientRepo;
    }

    @GetMapping("/stats")
    public DashboardStats stats(Authentication auth) {
        DashboardStats s = new DashboardStats();
        s.setTotalPatients(patientRepo.count());
        s.setTotalDoctors(doctorRepo.count());

        boolean admin = auth.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
        if (admin) {
            s.setTotalAppointments(appointmentRepo.count());
            s.setPending(appointmentRepo.countByStatus(AppointmentStatus.PENDING));
            s.setConfirmed(appointmentRepo.countByStatus(AppointmentStatus.CONFIRMED));
            s.setCompleted(appointmentRepo.countByStatus(AppointmentStatus.COMPLETED));
            s.setCancelled(appointmentRepo.countByStatus(AppointmentStatus.CANCELLED));
            s.setTodayAppointments(appointmentRepo.countByAppointmentDate(LocalDate.now()));
            s.setRevenue(appointmentRepo.totalRevenue());
        } else {
            Doctor d = doctorRepo.findByUserUsername(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
            Long id = d.getId();
            s.setTotalAppointments(appointmentRepo.countByDoctorId(id));
            s.setPending(appointmentRepo.countByDoctorIdAndStatus(id, AppointmentStatus.PENDING));
            s.setConfirmed(appointmentRepo.countByDoctorIdAndStatus(id, AppointmentStatus.CONFIRMED));
            s.setCompleted(appointmentRepo.countByDoctorIdAndStatus(id, AppointmentStatus.COMPLETED));
            s.setCancelled(appointmentRepo.countByDoctorIdAndStatus(id, AppointmentStatus.CANCELLED));
            s.setRevenue(appointmentRepo.revenueForDoctor(id));
        }
        return s;
    }
}
