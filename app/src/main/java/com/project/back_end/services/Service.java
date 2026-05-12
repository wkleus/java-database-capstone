package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.model.Admin;
import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.TokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // Constructor Injection
    public Service(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService
    ) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // VALIDATE TOKEN
    public Map<String, String> validateToken(String token, String user) {

        Map<String, String> response = new HashMap<>();

        boolean valid = tokenService.validateToken(token, user);

        if (!valid) {
            response.put("message", "Invalid or expired token.");
        }

        return response; // empty map = valid
    }

    // VALIDATE ADMIN LOGIN
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {

        Map<String, String> response = new HashMap<>();

        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

            if (admin == null) {
                response.put("message", "Admin not found.");
                return ResponseEntity.status(401).body(response);
            }

            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid password.");
                return ResponseEntity.status(401).body(response);
            }

            String token = tokenService.generateToken(admin.getId(), "admin");
            response.put("token", token);
            response.put("message", "Login successful.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal server error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // FILTER DOCTOR (NAME, SPECIALTY, TIME)
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {

        if (name != null && specialty != null && time != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        }

        if (name != null && time != null) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        }

        if (name != null && specialty != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        }

        if (specialty != null && time != null) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        }

        if (specialty != null) {
            return doctorService.filterDoctorBySpecility(specialty);
        }

        if (time != null) {
            return doctorService.filterDoctorsByTime(time);
        }

        if (name != null) {
            return doctorService.findDoctorByName(name);
        }

        // Default: return all doctors
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctorService.getDoctors());
        return map;
    }

    // VALIDATE APPOINTMENT
    public int validateAppointment(Appointment appointment) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOpt.isEmpty()) {
            return -1; // doctor does not exist
        }

        LocalDate date = appointment.getAppointmentTime().toLocalDate();
        List<String> availableSlots =
                doctorService.getDoctorAvailability(doctorOpt.get().getId(), date);

        String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();

        return availableSlots.contains(requestedTime) ? 1 : 0;
    }

    // VALIDATE PATIENT (REGISTRATION)
    public boolean validatePatient(Patient patient) {

        Patient existing = patientRepository.findByEmailOrPhone(
                patient.getEmail(),
                patient.getPhone()
        );

        return existing == null; // true = valid (does not exist)
    }

    // VALIDATE PATIENT LOGIN
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {

        Map<String, String> response = new HashMap<>();

        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());

            if (patient == null) {
                response.put("message", "Patient not found.");
                return ResponseEntity.status(401).body(response);
            }

            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid password.");
                return ResponseEntity.status(401).body(response);
            }

            String token = tokenService.generateToken(patient.getId(), "patient");
            response.put("token", token);
            response.put("message", "Login successful.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal server error.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // FILTER PATIENT APPOINTMENTS
    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token
    ) {

        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Unauthorized.");
            return ResponseEntity.status(403).body(response);
        }

   Long patientId = patient.getId();

        if (condition != null && name != null) {
            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        }

        if (condition != null) {
            return patientService.filterByCondition(condition, patientId);
        }

        if (name != null) {
            return patientService.filterByDoctor(name, patientId);
        }

        // Default: return all appointments
        return patientService.getPatientAppointment(patientId, token);
    }
}
