package com.project.back_end.repo;

import com.project.back_end.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /* Find appointments for a doctor within a time range
       Includes doctor + availability using LEFT JOIN FETCH */
    @Query("""
            SELECT a FROM Appointment a
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH d.availableTimes
            WHERE d.id = :doctorId
            AND a.appointmentTime BETWEEN :start AND :end
            """)
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    /* Filter by doctor ID, patient name (case-insensitive) and time range */
    @Query("""
            SELECT a FROM Appointment a
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH a.patient p
            WHERE d.id = :doctorId
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%'))
            AND a.appointmentTime BETWEEN :start AND :end
            """)
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId,
            String patientName,
            LocalDateTime start,
            LocalDateTime end
    );

    /* Delete all appointments for a doctor */
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    /* Find all appointments for a patient */
    List<Appointment> findByPatientId(Long patientId);

    /* Find appointments by patient + status, ordered by appointment time */
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
            Long patientId,
            int status
    );

    /* Filter by doctor name + patient ID (case-insensitive partial match) */
    @Query("""
            SELECT a FROM Appointment a
            JOIN a.doctor d
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
            AND a.patient.id = :patientId
            """)
    List<Appointment> filterByDoctorNameAndPatientId(
            String doctorName,
            Long patientId
    );

    /*Filter by doctor name + patient ID + status */
    @Query("""
            SELECT a FROM Appointment a
            JOIN a.doctor d
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
            AND a.patient.id = :patientId
            AND a.status = :status
            """)
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            String doctorName,
            Long patientId,
            int status
    );
}
