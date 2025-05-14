package com.zimenina.yuliya.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordEntry {
    @JsonProperty("service")
    private String service;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("isPasswordVisible")
    private boolean isPasswordVisible;

    public PasswordEntry() {
    }

    public PasswordEntry(String service, String username, String password) {
        this.service = service;
        this.username = username;
        this.password = password;
        this.isPasswordVisible = false;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password; // Всегда реальный пароль
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayedPassword() {
        return isPasswordVisible ? password : "******"; // Для отображения в таблице
    }

    public boolean isPasswordVisible() {
        return isPasswordVisible;
    }

    public void setPasswordVisible(boolean passwordVisible) {
        isPasswordVisible = passwordVisible;
    }
}