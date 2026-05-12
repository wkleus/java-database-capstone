package com.project.back_end.services;

import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    // Constructor Injection
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    // BOOK APPOINTMENT
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // failure
        }
    }

    // UPDATE APPOINTMENT
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existing = existingOpt.get();

        // Validate patient identity
        if (!Objects.equals(existing.getPatient().getId(), appointment.getPatient().getId())) {
            response.put("message", "Unauthorized: Patient mismatch.");
            return ResponseEntity.status(403).body(response);
        }

        // Validate doctor exists
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOpt.isEmpty()) {
            response.put("message", "Invalid doctor ID.");
            return ResponseEntity.badRequest().body(response);
        }

        // Validate patient exists
        Optional<Patient> patientOpt = patientRepository.findById(appointment.getPatient().getId());
        if (patientOpt.isEmpty()) {
            response.put("message", "Invalid patient ID.");
            return ResponseEntity.badRequest().body(response);
        }

        // Update fields
        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        existing.setDoctor(doctorOpt.get());
        existing.setPatient(patientOpt.get());

        appointmentRepository.save(existing);

        response.put("message", "Appointment updated successfully.");
        return ResponseEntity.ok(response);
    }

    // CANCEL APPOINTMENT
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = opt.get();

        // Validate patient identity from token
        Long patientIdFromToken = tokenService.extractUserId(token);
        if (!Objects.equals(patientIdFromToken, appointment.getPatient().getId())) {
            response.put("message", "Unauthorized: You cannot cancel this appointment.");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);

        response.put("message", "Appointment cancelled successfully.");
        return ResponseEntity.ok(response);
    }

    // GET APPOINTMENTS FOR DOCTOR ON SPECIFIC DATE
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {

        Map<String, Object> result = new HashMap<>();

        Long doctorId = tokenService.extractUserId(token);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> appointments;

        if (pname == null || pname.equalsIgnoreCase("null") || pname.isBlank()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    doctorId, start, end
            );
        } else {
            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, pname, start, end
                    );
        }

        result.put("appointments", appointments);
        return result;
    }

    // CHANGE STATUS
    @Transactional
    public void changeStatus(long id, int status) {
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isPresent()) {
            Appointment appointment = opt.get();
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
        }
    }
}
