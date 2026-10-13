package com.smarthospital.repository;

import com.smarthospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPhone(String phone);
}
