# Password Manager
***(IN PROGRESS)***

** This project demonstrates work with GUI (JavaFX 21), security, files, and data encryption.**

## Table of contents
1. [Description](#description)
2. [Tehnologies](#technologies)
3. [Installation and Launch](#installation-and-launch)
4. [Author](#author)
5. [License](#license)
6. [Usage Examples](#usage-examples)

## Description
The Password Manager project was developed to reinforce skills in working with GUI (JavaFX), data encryption,
files, and security tools.

### Main functions:
- Create/add a new password (service name, login, password).
- View a list of saved passwords (service name, login, password).
- Search by service name.
- Delete/edit a record.

## Technologies
- **Java 21**
- **JavaFX** (GUI)
- **Java Cryptography** (Data encryption, AES)
- **JSON** (Data storage)
- **Maven** (Build)

## Installation and Launch
1. Clone the repository:
   
   ```bash
   git clone https://github.com/YuliyaZimenina/PassworManager.git
   cd PasswordManager
   ```
3. Open the project in your favorite IDE (IntelliJ IDEA, Eclipse, etc.).
4. Run the application (for example:
   - Open the terminal in IntelliJ IDEA
   - Type the command:
     
   ```bash
   mvn javafx:run
   ```

## Author

[Yuliya Zimenina](https://github.com/YuliyaZimenina)

## License

This project is distributed under the MIT license.

## Usage Examples

**Main class:**
```java

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for the Password Manager application.
 * This class initializes the JavaFX application and loads the main FXML file.
 */
public class Main extends Application {
    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs during loading the FXML file
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setTitle("Password Manager");
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

