// footer.js — static reusable footer component

function renderFooter() {
  const footer = document.getElementById("footer");
  if (!footer) return;

  footer.innerHTML = `
    <footer class="footer">
      <div class="footer-container">

        <!-- Logo links -->
        <div class="footer-logo">
          <img src="./assets/images/logo/logo.png" alt="Hospital CMS Logo">
        </div>

        <!-- Link-Spalten rechts -->
        <div class="footer-links">
          <div class="footer-column">
            <h4>Company</h4>
            <a href="#">About</a>
            <a href="#">Careers</a>
            <a href="#">Press</a>
          </div>
          <div class="footer-column">
            <h4>Support</h4>
            <a href="#">Account</a>
            <a href="#">Help Center</a>
            <a href="#">Contact</a>
          </div>
          <div class="footer-column">
            <h4>Legals</h4>
            <a href="#">Terms & Conditions</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Licensing</a>
          </div>
        </div>

      </div>

      <!-- Copyright-Zeile ganz unten, einzeilig -->
      <div class="footer-bottom">
        Providing modern healthcare solutions for everyone. &nbsp;&nbsp;© 2024 Smart Clinic. All rights reserved.
      </div>
    </footer>
  `;
}

document.addEventListener("DOMContentLoaded", renderFooter);