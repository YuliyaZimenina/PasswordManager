package com.zimenina.yuliya;

import com.zimenina.yuliya.controller.MasterLoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.zimenina.yuliya.util.AESUtil;

import java.io.*;
import java.util.Properties;

/**
 * Manages the master password for the Password Manager application.
 * Handles password setup, verification, and login attempt limits.
 */
public class MasterPasswordManager {
    private static final String PROPERTIES_FILE = "/application.properties";
    private static final String USER_PROPERTIES_FILE = System.getProperty("user.home") + "/password_manager.properties";
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private int loginAttempts = 0;
    private Properties properties;

    /**
     * Loads the application properties and initiates the master password process.
     * @param primaryStage The main stage for the application
     * @return The verified master password, or null if authentication fails
     */
    public String authenticate(Stage primaryStage) {
        properties = loadProperties();
        String encryptedMasterPassword = properties.getProperty("encryptedMasterPassword");

        if (encryptedMasterPassword == null || encryptedMasterPassword.isEmpty()) {
            return promptSetMasterPassword(primaryStage);
        } else {
            return promptLogin(encryptedMasterPassword, primaryStage);
        }
    }

    /**
     * Prompts the user to set a new master password if none exists.
     */
    private String promptSetMasterPassword(Stage primaryStage) {
        try {
            Stage setPasswordStage = new Stage();
            setPasswordStage.setTitle("Set Master Password");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/master_login.fxml"));
            Scene scene = new Scene(loader.load(), 300, 150);
            setPasswordStage.setScene(scene);

            MasterLoginController controller = loader.getController();
            final String[] newPassword = {null};
            controller.setOnLoginSuccess(password -> {
                String encryptedPassword = AESUtil.encrypt(password, password);
                properties.setProperty("encryptedMasterPassword", encryptedPassword);
                saveProperties(properties);
                newPassword[0] = password;
                setPasswordStage.close();
            });

            controller.setSetupMode(true);
            setPasswordStage.showAndWait();
            return newPassword[0];
        } catch (IOException e) {
            showAlert("Error", "Failed to load the password setup window.");
            return null;
        }
    }

    /**
     * Prompts the user to enter the master password with attempt limit.
     */
    private String promptLogin(String encryptedMasterPassword, Stage primaryStage) {
        try {
            Stage loginStage = new Stage();
            loginStage.setTitle("Enter Master Password");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/master_login.fxml"));
            Scene scene = new Scene(loader.load(), 300, 150);
            loginStage.setScene(scene);

            // Set the stage to be modal
            MasterLoginController controller = loader.getController();
            final String[] inputPassword = {null};
            controller.setOnLoginSuccess(password -> {
                String encryptedInput = AESUtil.encrypt(password, password);
                if (encryptedInput.equals(encryptedMasterPassword)) {
                    loginAttempts = 0;
                    inputPassword[0] = password;
                    loginStage.close();
                } else {
                    loginAttempts++;
                    if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                        showAlert("Error", "Maximum login attempts exceeded. Closing application.");
                        loginStage.close();
                        primaryStage.close();
                    } else {
                        showAlert("Error", "Incorrect master password. " + (MAX_LOGIN_ATTEMPTS - loginAttempts) + " attempts left.");
                    }
                }
            });

            loginStage.showAndWait();
            return inputPassword[0];
        } catch (IOException e) {
            showAlert("Error", "Failed to load the login window.");
            return null;
        }
    }

    /**
     * Loads the application properties file.
     * First tries to load from user home directory, then falls back to classpath.
     */
    private Properties loadProperties() {
        Properties properties = new Properties();

        // Try loading from user home directory first
        File userFile = new File(USER_PROPERTIES_FILE);
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile)) {
                properties.load(fis);
                return properties;
            } catch (IOException e) {
                showAlert("Warning", "Failed to load properties from user directory. Falling back to default.");
            }
        }

        // Fallback to classpath resource
        try (InputStream inputStream = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                // If the resource is not found, return empty properties
                showAlert("Info", "Properties file not found in classpath. Using default settings.");
            }
        } catch (IOException e) {
            showAlert("Warning", "Failed to load properties from classpath. Using default settings.");
        }
        return properties;
    }

    /**
     * Saves the updated properties to the user home directory.
     */
    private void saveProperties(Properties properties) {
        try {
            File userFile = new File(USER_PROPERTIES_FILE);
            userFile.getParentFile().mkdirs(); // Ensure parent directories exist
            try (FileOutputStream fos = new FileOutputStream(userFile)) {
                properties.store(fos, "Password Manager Properties");
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to save master password: " + e.getMessage());
        }
    }

    /**
     * Displays an alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}