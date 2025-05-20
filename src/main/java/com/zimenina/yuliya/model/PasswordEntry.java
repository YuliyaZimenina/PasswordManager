package com.zimenina.yuliya.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordEntry {
    // Service name for the password entry (e.g., website or application name)
    @JsonProperty("service")
    private String service;

    // Username associated with the password entry
    @JsonProperty("username")
    private String username;

    // Password for the entry
    @JsonProperty("password")
    private String password;

    // Flag indicating whether the password is visible or masked
    @JsonProperty("isPasswordVisible")
    private boolean isPasswordVisible;

    // Default constructor for JSON deserialization
    public PasswordEntry() {
    }

    // Constructor to initialize a password entry with service, username, and password
    public PasswordEntry(String service, String username, String password) {
        this.service = service;
        this.username = username;
        this.password = password;
        this.isPasswordVisible = false; // Password is hidden by default
    }

    // Returns the service name
    public String getService() {
        return service;
    }

    // Sets the service name
    public void setService(String service) {
        this.service = service;
    }

    // Returns the username
    public String getUsername() {
        return username;
    }

    // Sets the username
    public void setUsername(String username) {
        this.username = username;
    }

    // Returns the actual password
    public String getPassword() {
        return password; // Always returns the real password
    }

    // Sets the password
    public void setPassword(String password) {
        this.password = password;
    }

    // Returns the password for display (either the actual password or a masked version)
    public String getDisplayedPassword() {
        return isPasswordVisible ? password : "******"; // For display in a table
    }

    // Returns whether the password is visible
    public boolean isPasswordVisible() {
        return isPasswordVisible;
    }

    // Sets the visibility of the password
    public void setPasswordVisible(boolean passwordVisible) {
        isPasswordVisible = passwordVisible;
    }
}