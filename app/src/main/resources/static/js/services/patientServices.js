/*
  patientServices.js
  Centralizes all API communication related to patient data:
  - Signup
  - Login
  - Fetching patient profile
  - Fetching appointments
  - Filtering appointments
*/

import { API_BASE_URL } from "../config/config.js";

/* Base endpoint for patient APIs */
const PATIENT_API = API_BASE_URL + "/patient";

/* PATIENT SIGNUP - Used on signup page to register a new patient */
export async function patientSignup(data) {
  try {
    const response = await fetch(`${PATIENT_API}/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });

    const result = await response.json();

    return {
      success: response.ok,
      message: result.message || "Unknown response"
    };

  } catch (error) {
    console.error("Signup error:", error);
    return {
      success: false,
      message: "Server error during signup."
    };
  }
}

/* PATIENT LOGIN - Used on login modal to authenticate patient */
export async function patientLogin(data) {
  try {
    const response = await fetch(`${PATIENT_API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });

    return response; // frontend handles token + role

  } catch (error) {
    console.error("Patient login error:", error);
    return null;
  }
}

/* GET LOGGED-IN PATIENT DATA - Requires token stored in localStorage */
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/me`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": token
      }
    });

    if (!response.ok) return null;

    const data = await response.json();
    return data.patient || null;

  } catch (error) {
    console.error("Error fetching patient data:", error);
    return null;
  }
}

/* GET PATIENT APPOINTMENTS - Works for both patient & doctor dashboards */
export async function getPatientAppointments(id, token, user) {
  try {
    const url = `${PATIENT_API}/appointments/${user}/${id}`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": token
      }
    });

    if (!response.ok) return null;

    const data = await response.json();
    return data.appointments || [];

  } catch (error) {
    console.error("Error fetching appointments:", error);
    return null;
  }
}

/* FILTER APPOINTMENTS - Supports filtering by condition + patient name */
export async function filterAppointments(condition, name, token) {
  try {
    const url = `${PATIENT_API}/appointments/filter?condition=${condition}&name=${name}`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": token
      }
    });

    if (!response.ok) {
      console.warn("Failed to filter appointments.");
      return [];
    }

    const data = await response.json();
    return data.appointments || [];

  } catch (error) {
    console.error("Error filtering appointments:", error);
    alert("Unexpected error while filtering appointments.");
    return [];
  }
}
