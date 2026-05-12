package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.model.Appointment;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Constructor Injection
    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // CREATE PATIENT
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // failure
        }
    }

    // GET PATIENT APPOINTMENTS
    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {

        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || !Objects.equals(patient.getId(), id)) {
            response.put("message", "Unauthorized access.");
            return ResponseEntity.status(403).body(response);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);

        List<AppointmentDTO> dtoList = appointments.stream()
                .map(a -> new AppointmentDTO(
                        a.getId(),
                        a.getDoctor().getId(),
                        a.getDoctor().getName(),
                        a.getPatient().getId(),
                        a.getPatient().getName(),
                        a.getPatient().getEmail(),
                        a.getPatient().getPhone(),
                        a.getPatient().getAddress(),
                        a.getAppointmentTime(),
                        a.getStatus()
                ))
                .toList();

        response.put("appointments", dtoList);
        return ResponseEntity.ok(response);
    }

    // FILTER BY CONDITION (PAST / FUTURE)
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {

        Map<String, Object> response = new HashMap<>();

        int status;
        if (condition.equalsIgnoreCase("past")) {
            status = 1;
        } else if (condition.equalsIgnoreCase("future")) {
            status = 0;
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);

        List<AppointmentDTO> dtoList = appointments.stream()
                .map(a -> new AppointmentDTO(
                        a.getId(),
                        a.getDoctor().getId(),
                        a.getDoctor().getName(),
                        a.getPatient().getId(),
                        a.getPatient().getName(),
                        a.getPatient().getEmail(),
                        a.getPatient().getPhone(),
                        a.getPatient().getAddress(),
                        a.getAppointmentTime(),
                        a.getStatus()
                ))
                .toList();

        response.put("appointments", dtoList);
        return ResponseEntity.ok(response);
    }

    // FILTER BY DOCTOR NAME
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {

        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);

        List<AppointmentDTO> dtoList = appointments.stream()
                .map(a -> new AppointmentDTO(
                        a.getId(),
                        a.getDoctor().getId(),
                        a.getDoctor().getName(),
                        a.getPatient().getId(),
                        a.getPatient().getName(),
                        a.getPatient().getEmail(),
                        a.getPatient().getPhone(),
                        a.getPatient().getAddress(),
                        a.getAppointmentTime(),
                        a.getStatus()
                ))
                .toList();

        response.put("appointments", dtoList);
        return ResponseEntity.ok(response);
    }

    // FILTER BY DOCTOR NAME + CONDITION
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(
            String condition, String name, long patientId) {

        Map<String, Object> response = new HashMap<>();

        int status;
        if (condition.equalsIgnoreCase("past")) {
            status = 1;
        } else if (condition.equalsIgnoreCase("future")) {
            status = 0;
        } else {
            response.put("message", "Invalid condition.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

        List<AppointmentDTO> dtoList = appointments.stream()
                .map(a -> new AppointmentDTO(
                        a.getId(),
                        a.getDoctor().getId(),
                        a.getDoctor().getName(),
                        a.getPatient().getId(),
                        a.getPatient().getName(),
                        a.getPatient().getEmail(),
                        a.getPatient().getPhone(),
                        a.getPatient().getAddress(),
                        a.getAppointmentTime(),
                        a.getStatus()
                ))
                .toList();

        response.put("appointments", dtoList);
        return ResponseEntity.ok(response);
    }

    // GET PATIENT DETAILS
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {

        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            response.put("message", "Patient not found.");
            return ResponseEntity.status(404).body(response);
        }

        response.put("patient", patient);
        return ResponseEntity.ok(response);
    }
}
