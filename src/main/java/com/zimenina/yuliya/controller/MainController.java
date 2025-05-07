package com.zimenina.yuliya.controller;

import com.zimenina.yuliya.model.PasswordEntry;
import com.zimenina.yuliya.util.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MainController is responsible for handling the main application logic,
 * including adding, editing, deleting, and saving password entries.
 */
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // FXML components
    @FXML private TableView<PasswordEntry> tableView;
    @FXML private TableColumn<PasswordEntry, String> serviceColumn;
    @FXML private TableColumn<PasswordEntry, String> usernameColumn;
    @FXML private TableColumn<PasswordEntry, String> passwordColumn;
    @FXML private TextField serviceField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button togglePasswordButton;

    private TextField visiblePasswordField;
    private boolean passwordVisible = false;

    // Observable list to hold password entries
    private final ObservableList<PasswordEntry> passwordList = FXCollections.observableArrayList();

    /**
     * Initializes the controller and sets up the table columns.
     */
    @FXML
    public void initialize() {
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Setting up a password column with a toggle button
        passwordColumn.setCellFactory(col -> new TableCell<PasswordEntry, String>() {
            private final Button toggleButton = new Button("👁");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    PasswordEntry entry = getTableRow().getItem();
                    setText(null); // Clearing text using HBox

                    toggleButton.setText(entry.isPasswordVisible() ? "🙈" : "👁");
                    toggleButton.setOnAction(event -> {
                        entry.setPasswordVisible(!entry.isPasswordVisible());
                        getTableView().refresh(); // Updating the table
                    });

                    // Create a HBox to hold the password and toggle button
                    HBox hbox = new HBox(5);
                    hbox.getChildren().addAll(new Label(entry.getPassword()), toggleButton);
                    setGraphic(hbox);
                }
            }
        });

        // Load data from the JSON file
        loadData();
        tableView.setItems(passwordList);
        logger.info("Initialization complete. Records loaded:{}", passwordList.size());

        // Initialize the visible password field
        visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setPrefWidth(200);
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        // Adding the visible password field to the same parent as the password field
        StackPane parent = (StackPane) passwordField.getParent();
        parent.getChildren().add(visiblePasswordField);
    }


    /**
     * Loads password entries from the JSON file.
     */
    private void loadData() {
        try {
            passwordList.setAll(Storage.load());
            logger.info("Data successfully loaded from data.json. Number of records: {}", passwordList.size());
        } catch (Exception e) {
            logger.error("Error loading data: ", e);
            showAlert("Error", "Failed to load data. The data.json file may be corrupted.");
        }
    }

    /**
     * Handles the action of adding a new password entry.
     */
    @FXML
    private void onAdd() {
        String service = serviceField.getText();
        String username = usernameField.getText();
        String password = passwordVisible ? visiblePasswordField.getText() : passwordField.getText();

        if (!service.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            PasswordEntry entry = new PasswordEntry(service, username, password);
            passwordList.add(entry);
            clearFields();
            logger.info("New entry added: {}", service);
        }
    }

    /**
     * Handles the action of editing an existing password entry.
     */
    @FXML
    private void onEdit() {
        PasswordEntry selectedEntry = tableView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            serviceField.setText(selectedEntry.getService());
            usernameField.setText(selectedEntry.getUsername());
            if (passwordVisible) {
                visiblePasswordField.setText(selectedEntry.getPassword());
            } else {
                passwordField.setText(selectedEntry.getPassword());
            }
        } else {
            showAlert("Alert", "Please select a post to edit.");
        }
    }

    /**
     * Handles the action of deleting a password entry.
     */
    @FXML
    private void onDelete() {
        PasswordEntry selectedEntry = tableView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            passwordList.remove(selectedEntry);
            logger.info("Entry deleted: {}", selectedEntry.getService());
        } else {
            showAlert("Alert", "Please select an entry to delete.");
        }
    }

    /**
     * Handles the action of saving password entries to the JSON file.
     */
    @FXML
    private void onSave() {
        try {
            Storage.save(passwordList);
            showAlert("Save", "Data has been successfully saved to the data.json file.");
            logger.info("Data saved. Number of records: {}", passwordList.size());
        } catch (Exception e) {
            logger.error("Error saving data: ", e);
            showAlert("Error", "Failed to save data.");
        }
    }

    /**
     * Handles the action of toggling password visibility.
     */
    @FXML
    private void onTogglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);

            togglePasswordButton.setText("🙈");
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);

            togglePasswordButton.setText("👁");
        }
    }

    /**
     * Handles the action of clearing the input fields.
     */
    private void clearFields() {
        serviceField.clear();
        usernameField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
    }

    /**
     * Handles the action of clearing the input fields when the clear button is clicked.
     */
    @FXML
    private void onClear() {
        clearFields();
        logger.info("Fields cleared by user.");
    }

    /**
     * Displays an alert dialog with the specified title and message.
     *
     * @param title   The title of the alert dialog.
     * @param message The message to be displayed in the alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}