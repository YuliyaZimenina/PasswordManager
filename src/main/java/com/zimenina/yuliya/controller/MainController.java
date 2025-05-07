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

public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

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

    private final ObservableList<PasswordEntry> passwordList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Настраиваем столбец пароля с кнопкой переключения
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
                    setText(null); // Очищаем текст, используем HBox

                    toggleButton.setText(entry.isPasswordVisible() ? "🙈" : "👁");
                    toggleButton.setOnAction(event -> {
                        entry.setPasswordVisible(!entry.isPasswordVisible());
                        getTableView().refresh(); // Обновляем таблицу
                    });

                    HBox hbox = new HBox(5);
                    hbox.getChildren().addAll(new Label(entry.getPassword()), toggleButton);
                    setGraphic(hbox);
                }
            }
        });

        loadData();
        tableView.setItems(passwordList);
        logger.info("Инициализация завершена. Загружено записей: {}", passwordList.size());

        visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setPrefWidth(200);
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        StackPane parent = (StackPane) passwordField.getParent();
        parent.getChildren().add(visiblePasswordField);
    }

    // Остальной код MainController.java остается без изменений
    private void loadData() {
        try {
            passwordList.setAll(Storage.load());
            logger.info("Данные успешно загружены из data.json. Количество записей: {}", passwordList.size());
        } catch (Exception e) {
            logger.error("Ошибка загрузки данных: ", e);
            showAlert("Ошибка", "Не удалось загрузить данные. Файл data.json может быть поврежден.");
        }
    }

    @FXML
    private void onAdd() {
        String service = serviceField.getText();
        String username = usernameField.getText();
        String password = passwordVisible ? visiblePasswordField.getText() : passwordField.getText();

        if (!service.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            PasswordEntry entry = new PasswordEntry(service, username, password);
            passwordList.add(entry);
            clearFields();
            logger.info("Добавлена новая запись: {}", service);
        }
    }

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
            showAlert("Предупреждение", "Пожалуйста, выберите запись для редактирования.");
        }
    }

    @FXML
    private void onDelete() {
        PasswordEntry selectedEntry = tableView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            passwordList.remove(selectedEntry);
            logger.info("Запись удалена: {}", selectedEntry.getService());
        } else {
            showAlert("Предупреждение", "Пожалуйста, выберите запись для удаления.");
        }
    }

    @FXML
    private void onSave() {
        try {
            Storage.save(passwordList);
            showAlert("Сохранение", "Данные успешно сохранены в файл data.json.");
            logger.info("Данные сохранены. Количество записей: {}", passwordList.size());
        } catch (Exception e) {
            logger.error("Ошибка сохранения данных: ", e);
            showAlert("Ошибка", "Не удалось сохранить данные.");
        }
    }

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

    private void clearFields() {
        serviceField.clear();
        usernameField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
    }

    @FXML
    private void onClear() {
        clearFields();
        logger.info("Поля очищены пользователем.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}