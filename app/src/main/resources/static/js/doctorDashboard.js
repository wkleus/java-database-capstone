/*
  doctorDashboard.js
  Handles appointment management for doctors:
  - Loads appointments for selected date
  - Filters by patient name
  - Displays appointment rows dynamically
*/

/* IMPORTS */
import { getAllAppointments } from "../services/appointmentRecordService.js";
import { createPatientRow } from "../components/patientRows.js";

/* GLOBAL VARIABLES */

// Table body where rows will be rendered
const tableBody = document.getElementById("patientTableBody");

// Today's date in YYYY-MM-DD format
let selectedDate = new Date().toISOString().split("T")[0];

// Token for authentication
let token = localStorage.getItem("token");

// Patient name for search filtering
let patientName = "null";

/* SEARCH BAR FUNCTIONALITY */
const searchBar = document.getElementById("searchBar");

if (searchBar) {
  searchBar.addEventListener("input", () => {
    const value = searchBar.value.trim();

    patientName = value !== "" ? value : "null";

    loadAppointments();
  });
}

/* TODAY BUTTON FUNCTIONALITY */
const todayBtn = document.getElementById("todayBtn");

if (todayBtn) {
  todayBtn.addEventListener("click", () => {
    selectedDate = new Date().toISOString().split("T")[0];

    const datePicker = document.getElementById("dateFilter");
    if (datePicker) datePicker.value = selectedDate;

    loadAppointments();
  });
}

/* DATE PICKER FUNCTIONALITY */
const datePicker = document.getElementById("dateFilter");

if (datePicker) {
  datePicker.addEventListener("change", () => {
    selectedDate = datePicker.value;
    loadAppointments();
  });
}

/* LOAD APPOINTMENTS */
async function loadAppointments() {
  try {
    // Fetch appointments
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    // Clear table body
    tableBody.innerHTML = "";

    // No appointments found
    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="5" class="noPatientRecord">No Appointments found for today.</td>
        </tr>
      `;
      return;
    }

    // Render appointments
    appointments.forEach(app => {
      const patient = {
        id: app.patientId,
        name: app.patientName,
        phone: app.patientPhone,
        email: app.patientEmail,
        prescription: app.prescription
      };

      const row = createPatientRow(patient);
      tableBody.appendChild(row);
    });

  } catch (error) {
    console.error("Error loading appointments:", error);

    // Error fallback
    tableBody.innerHTML = `
      <tr>
        <td colspan="5" class="noPatientRecord">Error loading appointments. Try again later.</td>
      </tr>
    `;
  }
}

/* INITIAL PAGE LOAD */
document.addEventListener("DOMContentLoaded", () => {
  if (typeof renderContent === "function") {
    renderContent();
  }
  loadAppointments(); // Load today's appointments by default
});
