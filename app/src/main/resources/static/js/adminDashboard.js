/*
  adminDashboard.js
  Handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form
*/

/* IMPORTS */
import { openModal, closeModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "../services/doctorServices.js";
import { createDoctorCard } from "../components/doctorCard.js";

/* EVENT BINDING — ADD DOCTOR BUTTON */
document.addEventListener("DOMContentLoaded", () => {
  const addBtn = document.getElementById("addDocBtn");
  if (addBtn) {
    addBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  loadDoctorCards(); // Load doctors on page load
  attachFilterListeners(); // Bind search + filter events
});

/* LOAD ALL DOCTOR CARDS */
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
  }
}

/* RENDER DOCTOR CARDS */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = `<p class="noDoctorRecord">No doctors found.</p>`;
    return;
  }

  doctors.forEach((doc) => {
    const card = createDoctorCard(doc);
    contentDiv.appendChild(card);
  });
}

/* FILTER EVENT LISTENERS */
function attachFilterListeners() {
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
}

/* FILTER DOCTORS ON CHANGE */
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar").value.trim();
    const time = document.getElementById("filterTime").value;
    const specialty = document.getElementById("filterSpecialty").value;

    const doctors = await filterDoctors(name, time, specialty);

    if (!doctors || doctors.length === 0) {
      document.getElementById("content").innerHTML =
        `<p class="noDoctorRecord">No doctors found with the given filters.</p>`;
      return;
    }

    renderDoctorCards(doctors);

  } catch (error) {
    console.error("Filter error:", error);
    alert("Error filtering doctors. Please try again.");
  }
}

/* ADD NEW DOCTOR (ADMIN) */
export async function adminAddDoctor() {
  try {
    // Collect form values
    const name = document.getElementById("docName").value;
    const email = document.getElementById("docEmail").value;
    const phone = document.getElementById("docPhone").value;
    const password = document.getElementById("docPassword").value;
    const specialty = document.getElementById("docSpecialty").value;

    // Collect availability checkboxes
    const timeCheckboxes = document.querySelectorAll(".time-checkbox:checked");
    const availableTimes = Array.from(timeCheckboxes).map(cb => cb.value);

    // Validate token
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Unauthorized. Please log in again.");
      return;
    }

    // Build doctor object
    const doctor = {
      name,
      email,
      phone,
      password,
      specialty,
      availableTimes
    };

    // Save doctor
    const result = await saveDoctor(doctor, token);

    if (result.success) {
      alert("Doctor added successfully!");
      closeModal();
      loadDoctorCards(); // Refresh list
    } else {
      alert("Failed to add doctor: " + result.message);
    }

  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("Unexpected error while adding doctor.");
  }
}
