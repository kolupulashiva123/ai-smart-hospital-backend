package com.smarthospital.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class AppointmentDtos {

    public static class BookingRequest {
        @NotBlank private String patientName;
        @NotNull @Min(0) @Max(120) private Integer age;
        @NotBlank private String gender;
        @NotBlank @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits") private String phone;
        @NotBlank @Size(max = 1000) private String issue;
        @NotNull private Long doctorId;
        @NotNull private LocalDate date;
        @NotBlank private String timeSlot;

        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getIssue() { return issue; }
        public void setIssue(String issue) { this.issue = issue; }
        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    }

    public static class StatusUpdate {
        @NotBlank private String status;
        private String notes;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class DoctorRequest {
        @NotBlank private String name;
        @NotBlank private String specialization;
        @NotNull @Positive private Double fee;
        @NotNull @Min(0) private Integer experienceYears;
        @NotBlank private String username;
        @NotBlank @Size(min = 6) private String password;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }
        public Double getFee() { return fee; }
        public void setFee(Double fee) { this.fee = fee; }
        public Integer getExperienceYears() { return experienceYears; }
        public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
