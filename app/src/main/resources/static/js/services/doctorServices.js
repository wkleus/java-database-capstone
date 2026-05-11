/*
  doctorServices.js
  Handles all API interactions related to doctor data.
*/

import { API_BASE_URL } from "../config/config.js";

/* Base endpoint for doctor APIs */
const DOCTOR_API = API_BASE_URL + "/doctor";

/* GET ALL DOCTORS - Used by Admin Dashboard & Patient Dashboard */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API, {
      method: "GET",
      headers: { "Content-Type": "application/json" }
    });

    if (!response.ok) {
      console.warn("Failed to fetch doctors.");
      return [];
    }

    const data = await response.json();
    return data.doctors || [];

  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/* DELETE DOCTOR (Admin Only) - Requires doctor ID + admin token */
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}?token=${token}`, {
      method: "DELETE",
      headers: { "Content-Type": "application/json" }
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || "Unknown response"
    };

  } catch (error) {
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: "Server error while deleting doctor."
    };
  }
}

/* SAVE / ADD NEW DOCTOR (Admin Only)- Requires doctor object + admin token */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": token
      },
      body: JSON.stringify(doctor)
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || "Unknown response"
    };

  } catch (error) {
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: "Server error while saving doctor."
    };
  }
}

/* FILTER DOCTORS - Supports name, time (AM/PM), and specialty filters */
export async function filterDoctors(name = "", time = "", specialty = "") {
  try {
    const url = `${DOCTOR_API}/filter?name=${name}&time=${time}&specialty=${specialty}`;

    const response = await fetch(url, {
      method: "GET",
      headers: { "Content-Type": "application/json" }
    });

    if (!response.ok) {
      console.warn("Failed to filter doctors.");
      return [];
    }

    const data = await response.json();
    return data.doctors || [];

  } catch (error) {
    console.error("Error filtering doctors:", error);
    return [];
  }
}
