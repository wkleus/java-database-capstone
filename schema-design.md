# Clinic Management System – Database Schema Design

This document describes the database architecture for the Clinic Management System.  
It is divided into two major parts:

1. **MySQL Database Design** – for structured, relational, operational data  
2. **MongoDB Collection Design** – for flexible, semi‑structured or unstructured data

---

## MySQL Database Design

The following tables represent the core of the system.



### Table: doctors
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT  
- `name`: VARCHAR(100), NOT NULL  
- `email`: VARCHAR(150), UNIQUE, NOT NULL  
- `password_hash`: VARCHAR(255), NOT NULL  
- `specialization`: VARCHAR(100), NOT NULL  
- `phone`: VARCHAR(20), NULL  
- `created_at`: DATETIME, DEFAULT CURRENT_TIMESTAMP  

**Reasoning:**  
Doctors are core entities. Email must be unique. Passwords are stored as hashes.

---

### Table: patients
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT  
- `name`: VARCHAR(100), NOT NULL  
- `email`: VARCHAR(150), UNIQUE, NOT NULL  
- `password_hash`: VARCHAR(255), NOT NULL  
- `phone`: VARCHAR(20), NULL  
- `date_of_birth`: DATE, NULL  
- `created_at`: DATETIME, DEFAULT CURRENT_TIMESTAMP  

**Reasoning:**  
Patients require login credentials and basic personal information.

---

### Table: appointments
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT  
- `doctor_id`: INT, FOREIGN KEY → doctors(id), NOT NULL  
- `patient_id`: INT, FOREIGN KEY → patients(id), NOT NULL  
- `appointment_time`: DATETIME, NOT NULL  
- `duration_minutes`: INT, DEFAULT 60  
- `status`: ENUM('scheduled', 'completed', 'cancelled'), DEFAULT 'scheduled'  
- `created_at`: DATETIME, DEFAULT CURRENT_TIMESTAMP  

**Constraints & Logic:**  
- A doctor cannot have overlapping appointments (enforced via backend logic or UNIQUE constraints).  
- Patient appointment history should remain even if the patient account is removed (medical record retention).

---

### Table: admin
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT  
- `username`: VARCHAR(100), UNIQUE, NOT NULL  
- `password_hash`: VARCHAR(255), NOT NULL  
- `created_at`: DATETIME, DEFAULT CURRENT_TIMESTAMP  

**Reasoning:**  
Admins manage the system and require secure authentication.

---

### Table: doctor_availability
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT  
- `doctor_id`: INT, FOREIGN KEY → doctors(id), NOT NULL  
- `start_time`: DATETIME, NOT NULL  
- `end_time`: DATETIME, NOT NULL  

**Reasoning:**  
Doctors define their available time slots.  
Patients can only book within these ranges.

---

### (Optional) Table: payments
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT  
- `patient_id`: INT, FOREIGN KEY → patients(id)  
- `appointment_id`: INT, FOREIGN KEY → appointments(id)  
- `amount`: DECIMAL(10,2), NOT NULL  
- `status`: ENUM('pending', 'paid', 'failed')  
- `created_at`: DATETIME, DEFAULT CURRENT_TIMESTAMP  

**Reasoning:**  
Useful for clinics that charge per appointment.

---

## MongoDB Collection Design


## MongoDB Collection Design

MongoDB is used for data types that don't fit well into rigid SQL tables, e.g.:

- Doctor notes  
- Patient feedback  
- Chat messages  
- Logs  
- Prescriptions  

---

### Collection: prescriptions

An example document for prescriptions:

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 51,
  "doctorId": 12,
  "appointmentId": 88,
  "medications": [
    {
      "name": "Ibuprofen",
      "dosage": "400mg",
      "frequency": "3x daily",
      "durationDays": 5
    }
  ],
  "doctorNotes": "Avoid heavy exercise during medication.",
  "refillCount": 1,
  "pharmacy": {
    "name": "City Pharmacy",
    "location": "Berlin Mitte"
  },
  "createdAt": "2025-01-15T10:30:00Z"
}
```

