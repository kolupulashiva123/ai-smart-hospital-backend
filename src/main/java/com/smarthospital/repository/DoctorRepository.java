package com.smarthospital.repository;

import com.smarthospital.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecializationIgnoreCase(String specialization);
    Optional<Doctor> findByUserUsername(String username);
}
