// patientDashboard.js

import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { openModal } from "./components/modals.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { patientSignup, patientLogin } from "./services/patientServices.js";

/* LOAD DOCTOR CARDS ON PAGE LOAD */
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  // Signup modal trigger
  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  // Login modal trigger
  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }
});

/* LOAD ALL DOCTORS */
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    doctors.forEach((doctor) => {
      const card = createDoctorCard(doctor);
      contentDiv.appendChild(card);
    });
  } catch (error) {
    console.error("Failed to load doctors:", error);
  }
}

/* FILTER INPUT LISTENERS */
document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

/* FILTER DOCTORS ON CHANGE */
async function filterDoctorsOnChange() {
  try {
    const nameInput = document.getElementById("searchBar").value.trim();
    const timeInput = document.getElementById("filterTime").value;
    const specialtyInput = document.getElementById("filterSpecialty").value;

    const name = nameInput || null;
    const time = timeInput || null;
    const specialty = specialtyInput || null;

    const doctors = await filterDoctors(name, time, specialty);
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    if (doctors.length > 0) {
      doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    } else {
      contentDiv.innerHTML = `<p>No doctors found with the given filters.</p>`;
    }
  } catch (error) {
    console.error("Failed to filter doctors:", error);
    alert("❌ An error occurred while filtering doctors.");
  }
}

/* PATIENT SIGNUP HANDLER */
window.signupPatient = async function () {
  try {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const data = { name, email, password, phone, address };

    const { success, message } = await patientSignup(data);

    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    } else {
      alert(message);
    }
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

/* PATIENT LOGIN HANDLER */
window.loginPatient = async function () {
  try {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = { email, password };

    const response = await patientLogin(data);

    if (response && response.ok) {
      const result = await response.json();

      localStorage.setItem("token", result.token);
      localStorage.setItem("userRole", "loggedPatient");

      window.location.href = "/pages/loggedPatientDashboard.html";
    } else {
      alert("❌ Invalid credentials!");
    }
  } catch (error) {
    console.error("Login failed:", error);
    alert("❌ Failed to login.");
  }
};
