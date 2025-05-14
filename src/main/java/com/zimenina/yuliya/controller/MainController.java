package com.zimenina.yuliya.controller;

import com.zimenina.yuliya.model.PasswordEntry;
import com.zimenina.yuliya.util.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("displayedPassword"));

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
                    setText(null);

                    toggleButton.setText(entry.isPasswordVisible() ? "🙈" : "👁");
                    toggleButton.setOnAction(event -> {
                        entry.setPasswordVisible(!entry.isPasswordVisible());
                        getTableView().refresh();
                    });

                    Label passwordLabel = new Label(item != null ? item : "");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    HBox hbox = new HBox(5);
                    hbox.getChildren().addAll(passwordLabel, spacer, toggleButton);
                    setGraphic(hbox);
                }
            }
        });

        loadData();
        tableView.setItems(passwordList);
        logger.info("Initialization complete. Records loaded:{}", passwordList.size());

        visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setPrefWidth(200);
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        StackPane parent = (StackPane) passwordField.getParent();
        parent.getChildren().add(visiblePasswordField);
    }

    // Load data from the JSON file
    private void loadData() {
        try {
            passwordList.setAll(Storage.load());
            logger.info("Data successfully loaded from data.json. Number of records: {}", passwordList.size());
        } catch (Exception e) {
            logger.error("Error loading data: ", e);
            showAlert("Error", "Failed to load data. The data.json file may be corrupted.");
        }
    }

    // Add a new password entry
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

    // Update the selected entry with new values
    @FXML
    private void onEdit() {
        PasswordEntry selectedEntry = tableView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            serviceField.setText(selectedEntry.getService());
            usernameField.setText(selectedEntry.getUsername());

            String realPassword = selectedEntry.getPassword();
            System.out.println("Real password in onEdit: " + realPassword);

            if (passwordVisible) {
                visiblePasswordField.setText(realPassword);
            } else {
                passwordField.setText(realPassword); // PasswordField скрывает
            }

            passwordVisible = false;
            updatePasswordFieldVisibility();
        } else {
            showAlert("Alert", "Please select a post to edit.");
        }
    }

    // Update the selected entry with new values
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

    // Save the password list to a file
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

    // Toggle password visibility
    @FXML
    private void onTogglePasswordVisibility() {
        String currentPassword;
        if (passwordVisible) {
            currentPassword = visiblePasswordField.getText();
        } else {
            currentPassword = passwordField.getText();
        }

        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            visiblePasswordField.setText(currentPassword);  // Set the text of the visible password field
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🙈");
        } else {
            passwordField.setText(currentPassword); // Set the text of the password field
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            togglePasswordButton.setText("👁");
        }
    }

    // This method is called when the user types in the password field
    private void updatePasswordFieldVisibility() {
        if (passwordVisible) {
            visiblePasswordField.setVisible(true); // Show the visible password field
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🙈");
        } else {
            passwordField.setVisible(true); // Show the password field
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            togglePasswordButton.setText("👁");
        }
    }

    // Clear all input fields
    private void clearFields() {
        serviceField.clear();
        usernameField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
    }

    // Clear all fields
    @FXML
    private void onClear() {
        clearFields();
        logger.info("Fields cleared by user.");
    }

    // Show an alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}