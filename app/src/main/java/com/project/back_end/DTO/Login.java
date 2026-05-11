package com.project.back_end.DTO;

/**
 * DTO used for handling login requests.
 * Accepts a user identifier (email or username) and password.
 * No persistence annotations — this class is NOT an entity.
 */
public class Login {

    // The unique identifier used for login (email for doctor/patient, username for admin)
    private String identifier;

    // The password provided by the user
    private String password;

    // Default constructor
    public Login() {}

    // Getters and Setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
