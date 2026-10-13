package com.smarthospital.repository;

import com.smarthospital.model.Appointment;
import com.smarthospital.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByAppointmentDateDescIdDesc();
    List<Appointment> findByDoctorIdOrderByAppointmentDateDescIdDesc(Long doctorId);
    List<Appointment> findByPatientPhoneOrderByAppointmentDateDescIdDesc(String phone);
    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDate date, AppointmentStatus status);
    long countByStatus(AppointmentStatus status);
    long countByDoctorId(Long doctorId);
    long countByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    long countByAppointmentDate(LocalDate date);

    @Query("select coalesce(sum(a.fee),0) from Appointment a where a.status = com.smarthospital.model.AppointmentStatus.COMPLETED")
    Double totalRevenue();

    @Query("select coalesce(sum(a.fee),0) from Appointment a where a.doctor.id = ?1 and a.status = com.smarthospital.model.AppointmentStatus.COMPLETED")
    Double revenueForDoctor(Long doctorId);
}
