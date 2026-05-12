package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Constructor Injection
    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // GET DOCTOR AVAILABILITY
    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return Collections.emptyList();

        Doctor doctor = doctorOpt.get();
        List<String> availableSlots = new ArrayList<>(doctor.getAvailableTimes());

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> booked = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        for (Appointment a : booked) {
            String bookedTime = a.getAppointmentTime().toLocalTime().toString();
            availableSlots.removeIf(slot -> slot.equals(bookedTime));
        }

        return availableSlots;
    }

    // SAVE DOCTOR
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // doctor exists
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // internal error
        }
    }

    // UPDATE DOCTOR
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (existing.isEmpty()) return -1;

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // GET ALL DOCTORS
    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // DELETE DOCTOR
    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(id);
            if (doctorOpt.isEmpty()) return -1;

            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);

            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // VALIDATE DOCTOR LOGIN
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {

        Map<String, String> response = new HashMap<>();

        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        if (doctor == null) {
            response.put("message", "Doctor not found.");
            return ResponseEntity.status(404).body(response);
        }

        if (!doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid password.");
            return ResponseEntity.status(403).body(response);
        }

        String token = tokenService.generateToken(doctor.getId(), "doctor");
        response.put("token", token);
        response.put("message", "Login successful.");

        return ResponseEntity.ok(response);
    }

    // FIND DOCTOR BY NAME
    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> map = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        map.put("doctors", doctors);
        return map;
    }

    // FILTER DOCTORS BY NAME + SPECIALTY + TIME
    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // FILTER DOCTORS BY NAME + TIME
    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {

        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // FILTER DOCTORS BY NAME + SPECIALTY
    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // FILTER DOCTORS BY TIME + SPECIALTY
    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {

        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // FILTER DOCTORS BY SPECIALTY
    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {

        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // FILTER DOCTORS BY TIME ONLY
    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {

        List<Doctor> doctors = doctorRepository.findAll();
        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctors);
        return map;
    }

    // PRIVATE: FILTER DOCTORS BY AM/PM
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {

        List<Doctor> filtered = new ArrayList<>();

        for (Doctor d : doctors) {
            for (String time : d.getAvailableTimes()) {

                int hour = Integer.parseInt(time.split(":")[0]);

                boolean isAM = hour < 12;
                boolean isPM = hour >= 12;

                if ((amOrPm.equalsIgnoreCase("AM") && isAM) ||
                    (amOrPm.equalsIgnoreCase("PM") && isPM)) {

                    filtered.add(d);
                    break;
                }
            }
        }

        return filtered;
    }
}
