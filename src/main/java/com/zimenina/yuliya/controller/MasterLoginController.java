package com.zimenina.yuliya.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

/**
 * Controller for the master password login window.
 * Handles both setting a new master password and logging in with an existing one.
 */
public class MasterLoginController {
    @FXML private PasswordField masterPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label confirmLabel;
    @FXML private Button loginButton;

    private boolean setupMode = false;
    private java.util.function.Consumer<String> onLoginSuccess;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
    }

    /**
     * Sets the callback to be executed on successful login.
     */
    public void setOnLoginSuccess(java.util.function.Consumer<String> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    /**
     * Sets the controller to setup mode (for creating a new master password).
     */
    public void setSetupMode(boolean setupMode) {
        this.setupMode = setupMode;
        if (setupMode) {
            confirmPasswordField.setVisible(true);
            confirmLabel.setVisible(true);
            loginButton.setText("Set Password");
        } else {
            confirmPasswordField.setVisible(false);
            confirmLabel.setVisible(false);
            loginButton.setText("Login");
        }
    }

    /**
     * Handles the login or password setup action.
     */
    @FXML
    private void handleLogin() {
        String password = masterPasswordField.getText();
        if (setupMode) {
            String confirmPassword = confirmPasswordField.getText();
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "Both password fields must be filled.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                showAlert("Error", "Passwords do not match.");
                return;
            }
        }
        if (password.isEmpty()) {
            showAlert("Error", "Master password cannot be empty.");
            return;
        }
        if (onLoginSuccess != null) {
            onLoginSuccess.accept(password);
        }
    }

    /**
     * Displays an alert dialog.
     */
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}