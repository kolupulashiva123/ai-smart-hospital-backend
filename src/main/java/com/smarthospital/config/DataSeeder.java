package com.smarthospital.config;

import com.smarthospital.model.Doctor;
import com.smarthospital.model.Role;
import com.smarthospital.model.User;
import com.smarthospital.repository.DoctorRepository;
import com.smarthospital.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final DoctorRepository doctorRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository userRepo, DoctorRepository doctorRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo; this.doctorRepo = doctorRepo; this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        userRepo.save(new User("admin", encoder.encode("admin123"), "Hospital Admin", Role.ADMIN));

        addDoctor("dr.sharma", "Dr. Anil Sharma", "General Physician", 400, 12);
        addDoctor("dr.reddy", "Dr. Sneha Reddy", "Cardiologist", 900, 15);
        addDoctor("dr.rao", "Dr. Kiran Rao", "Neurologist", 800, 10);
        addDoctor("dr.iyer", "Dr. Meera Iyer", "Dermatologist", 600, 8);
        addDoctor("dr.khan", "Dr. Imran Khan", "Orthopedic", 700, 14);
        addDoctor("dr.nair", "Dr. Priya Nair", "Pediatrician", 500, 9);
        addDoctor("dr.verma", "Dr. Rahul Verma", "ENT", 550, 7);
        addDoctor("dr.devi", "Dr. Lakshmi Devi", "Gynecologist", 750, 18);

        System.out.println("==== Sample data created ====");
        System.out.println("Admin  -> admin / admin123");
        System.out.println("Doctor -> dr.sharma / doctor123 (all doctors use doctor123)");
    }

    private void addDoctor(String username, String name, String spec, double fee, int exp) {
        User u = new User(username, encoder.encode("doctor123"), name, Role.DOCTOR);
        Doctor d = new Doctor();
        d.setName(name);
        d.setSpecialization(spec);
        d.setFee(fee);
        d.setExperienceYears(exp);
        d.setAvailable(true);
        d.setUser(u);
        doctorRepo.save(d);
    }
}
