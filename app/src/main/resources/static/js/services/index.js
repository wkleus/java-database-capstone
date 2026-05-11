/* Services (API/Logic Handlers) in app/src/main/resources/static/js/services/index.js */

import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
import { selectRole } from "../render.js";

/* API ENDPOINTS */
const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

/* Ensure DOM elements are available after page load */
window.onload = function () {
  const adminBtn = document.getElementById("adminLogin");
  const doctorBtn = document.getElementById("doctorLogin");

  // Admin Login Button
  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      openModal("adminLogin");
    });
  }

  // Doctor Login Button
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      openModal("doctorLogin");
    });
  }
};

/* This function will be triggered when the admin submits their login credentials */
window.adminLoginHandler = async function () {
  try {
    // Get the entered username and password
    const username = document.getElementById("adminUsername").value;
    const password = document.getElementById("adminPassword").value;

    // Admin object with credentials
    const admin = { username, password };

    // Send a POST request to the ADMIN_API endpoint
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(admin),
    });

    // If the response is successful
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("admin"); // Save role + redirect
      return;
    }

    // Invalid credentials
    alert("Invalid admin credentials!");

  } catch (error) {
    // Error handling
    console.error("Admin login error:", error);
    alert("Something went wrong. Please try again.");
  }
};

/*
  This function will be triggered when a doctor submits their login credentials
*/
window.doctorLoginHandler = async function () {
  try {
    // Get the entered email and password
    const email = document.getElementById("doctorEmail").value;
    const password = document.getElementById("doctorPassword").value;

    // Doctor object with credentials
    const doctor = { email, password };

    // Send a POST request to the DOCTOR_API endpoint
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    // If login is successful
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("doctor");
      return;
    }

    // Invalid credentials
    alert("Invalid doctor credentials!");

  } catch (error) {
    // Error handling
    console.error("Doctor login error:", error);
    alert("Something went wrong. Please try again.");
  }
};
