// header.js — dynamic role-based header renderer

function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;
  
    // Auf der Startseite Session zurücksetzen
    if (window.location.pathname.endsWith("/") || window.location.pathname.endsWith("index.html")) {
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
    }
  
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");
  
    // Ungültige Session abfangen
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
      alert("Session expired or invalid login. Please log in again.");
      window.location.href = "/";
      return;
    }
  
    // Header IMMER aufbauen — auch wenn role null ist
    let navLinks = "";
  
    if (role === "admin") {
      navLinks = `
        <button id="addDocBtn" class="adminBtn">Add Doctor</button>
        <a href="#" id="logoutBtn">Logout</a>
      `;
    } else if (role === "doctor") {
      navLinks = `
        <a href="/doctor/dashboard" id="homeBtn">Home</a>
        <a href="#" id="logoutBtn">Logout</a>
      `;
    } else if (role === "patient") {
      navLinks = `
        <a href="/login" id="loginBtn">Login</a>
        <a href="/signup" id="signupBtn">Sign Up</a>
      `;
    } else if (role === "loggedPatient") {
      navLinks = `
        <a href="/pages/patientDashboard.html" id="homeBtn">Home</a>
        <a href="/pages/appointments.html" id="appointmentsBtn">Appointments</a>
        <a href="#" id="logoutPatientBtn">Logout</a>
      `;
    }
  
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-link">
          <img src="./assets/images/logo/logo.png" class="logo-img" alt="Hospital CMS Logo">
          <span class="logo-title">Hospital CMS</span>
        </div>
        <nav class="nav">${navLinks}</nav>
      </header>
    `;
  
    attachHeaderButtonListeners();
  }
  
  function attachHeaderButtonListeners() {
    const addDocBtn      = document.getElementById("addDocBtn");
    const logoutBtn      = document.getElementById("logoutBtn");
    const logoutPatientBtn = document.getElementById("logoutPatientBtn");
  
    if (addDocBtn) {
      addDocBtn.addEventListener("click", () => {
        if (typeof openModal === "function") openModal("addDoctor");
      });
    }
    if (logoutBtn)        logoutBtn.addEventListener("click", logout);
    if (logoutPatientBtn) logoutPatientBtn.addEventListener("click", logoutPatient);
  }
  
  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
  }
  
  function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
  }
  
  document.addEventListener("DOMContentLoaded", renderHeader);