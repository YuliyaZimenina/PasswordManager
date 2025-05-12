package com.zimenina.yuliya;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.zimenina.yuliya.util.AESUtil;
import java.io.IOException;

/**
 * Main class for the Password Manager application.
 * Initializes the application and delegates master password handling.
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        MasterPasswordManager passwordManager = new MasterPasswordManager();
        String masterPassword = passwordManager.authenticate(primaryStage);

        if (masterPassword != null) {
            loadMainWindow(primaryStage, masterPassword);
        } else {
            primaryStage.close();
        }
    }

    /**
     * Loads the main application window after successful authentication.
     */
    private void loadMainWindow(Stage primaryStage, String masterPassword) {
        try {
            AESUtil.setMasterPassword(masterPassword);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            primaryStage.setTitle("Password Manager");
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load the main window.");
            alert.showAndWait();
            primaryStage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}