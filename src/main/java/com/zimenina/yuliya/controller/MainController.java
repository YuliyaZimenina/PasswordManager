package com.zimenina.yuliya.controller;

import com.zimenina.yuliya.model.PasswordEntry;
import com.zimenina.yuliya.util.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    @FXML private TextField searchField;

    private TextField visiblePasswordField;
    private boolean passwordVisible = false;

    // Observable list to hold password entries
    private final ObservableList<PasswordEntry> passwordList = FXCollections.observableArrayList();
    private FilteredList<PasswordEntry> filteredList;

    /**
     * Initializes the controller and sets up the table columns.
     */
    @FXML
    public void initialize() {
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("displayedPassword"));

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
        filteredList = new FilteredList<>(passwordList, p -> true);
        tableView.setItems(filteredList);
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
            showAlert("Ошибка", "Не удалось загрузить данные. Файл data.json может быть поврежден.");
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
                passwordField.setText(realPassword);
            }

            passwordVisible = false;
            updatePasswordFieldVisibility();
        } else {
            showAlert("Предупреждение", "Пожалуйста, выберите запись для редактирования.");
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
            showAlert("Предупреждение", "Пожалуйста, выберите запись для удаления.");
        }
    }

    // Save the password list to a file
    @FXML
    private void onSave() {
        try {
            Storage.save(passwordList);
            showAlert("Сохранение", "Данные успешно сохранены в файл data.json.");
            logger.info("Data saved. Number of records: {}", passwordList.size());
        } catch (Exception e) {
            logger.error("Error saving data: ", e);
            showAlert("Ошибка", "Не удалось сохранить данные.");
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
            visiblePasswordField.setText(currentPassword);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🙈");
        } else {
            passwordField.setText(currentPassword);
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
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🙈");
        } else {
            passwordField.setVisible(true);
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

    // Search functionality
    @FXML
    private void onSearch() {
        String searchText = searchField.getText().toLowerCase();
        filteredList.setPredicate(entry -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            return entry.getService().toLowerCase().contains(searchText);
        });
        logger.info("Search performed for: {}", searchText);
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