package com.zimenina.yuliya.model;

public class PasswordEntry {
    private String service;
    private String username;
    private String password;
    private boolean passwordVisible; // New field to track visibility

    public PasswordEntry(String service, String username, String password) {
        this.service = service;
        this.username = username;
        this.password = password;
        this.passwordVisible = false; // Default to hidden
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getService() {
        return service;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return passwordVisible ? password : "****"; // Mask password if not visible
    }

    public boolean isPasswordVisible() {
        return passwordVisible;
    }

    public void setPasswordVisible(boolean visible) {
        this.passwordVisible = visible;
    }
}