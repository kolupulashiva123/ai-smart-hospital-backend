package com.smarthospital.controller;

import com.smarthospital.dto.AuthDtos.LoginRequest;
import com.smarthospital.dto.AuthDtos.LoginResponse;
import com.smarthospital.exception.BadRequestException;
import com.smarthospital.model.User;
import com.smarthospital.repository.DoctorRepository;
import com.smarthospital.repository.UserRepository;
import com.smarthospital.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final DoctorRepository doctorRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo, DoctorRepository doctorRepo,
                          PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo; this.doctorRepo = doctorRepo; this.encoder = encoder; this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User user = userRepo.findByUsername(req.getUsername()).orElse(null);
        if (user == null || !encoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", 401, "message", "Invalid username or password"));
        }
        Long doctorId = doctorRepo.findByUserUsername(user.getUsername()).map(d -> d.getId()).orElse(null);
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, user.getRole().name(), user.getFullName(), doctorId));
    }
}
