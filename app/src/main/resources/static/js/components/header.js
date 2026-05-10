// header.js — dynamic role‑based header renderer

export function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  let headerContent = "";

  // Detect homepage → reset session
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Invalid session handling
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // Build header layout
  headerContent += `
    <header class="header">
      <div class="logo">Smart Clinic</div>
      <nav class="nav">
  `;

  // Role‑based header injection
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  }

  else if (role === "doctor") {
    headerContent += `
      <a href="/doctor/dashboard" id="homeBtn">Home</a>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  }

  else if (role === "patient") {
    headerContent += `
      <a href="/login" id="loginBtn">Login</a>
      <a href="/signup" id="signupBtn">Sign Up</a>
    `;
  }

  else if (role === "loggedPatient") {
    headerContent += `
      <a href="/pages/patientDashboard.html" id="homeBtn">Home</a>
      <a href="/pages/appointments.html" id="appointmentsBtn">Appointments</a>
      <a href="#" id="logoutPatientBtn">Logout</a>
    `;
  }

  headerContent += `
      </nav>
    </header>
  `;

  // Inject header into DOM
  headerDiv.innerHTML = headerContent;

  // Attach listeners
  attachHeaderButtonListeners();
}

// Attach event listeners after header is rendered
function attachHeaderButtonListeners() {
  const addDocBtn = document.getElementById("addDocBtn");
  const logoutBtn = document.getElementById("logoutBtn");
  const logoutPatientBtn = document.getElementById("logoutPatientBtn");

  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => {
      if (typeof openModal === "function") {
        openModal("addDoctor");
      }
    });
  }

  if (logoutBtn) {
    logoutBtn.addEventListener("click", logout);
  }

  if (logoutPatientBtn) {
    logoutPatientBtn.addEventListener("click", logoutPatient);
  }
}

// Logout for admin & doctor
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

// Logout for loggedPatient → revert to patient role
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

// Auto‑render header on page load
document.addEventListener("DOMContentLoaded", renderHeader);
