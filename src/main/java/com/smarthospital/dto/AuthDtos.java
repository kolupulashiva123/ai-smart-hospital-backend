package com.smarthospital.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private String role;
        private String name;
        private Long doctorId;
        public LoginResponse(String token, String role, String name, Long doctorId) {
            this.token = token; this.role = role; this.name = name; this.doctorId = doctorId;
        }
        public String getToken() { return token; }
        public String getRole() { return role; }
        public String getName() { return name; }
        public Long getDoctorId() { return doctorId; }
    }
}
