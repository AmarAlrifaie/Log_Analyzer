package javafxapplication1;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class JavaFXApplication1 extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        Label instructionLabel = new Label("Please enter the path of the file containing paths to the log files:");
        TextField pathInput = new TextField();

        Button submitButton = new Button("OK");
        submitButton.setOnAction(e -> {
            String indexFilePath = pathInput.getText();
            List<LogRecord> mergedRecords = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(indexFilePath))) {
                String logFilePath;
                while ((logFilePath = reader.readLine()) != null) {
                    LogFileReader logFileReader = new LogFileReader();
                    List<LogRecord> records = logFileReader.readLogFile(logFilePath.trim());
                    mergedRecords.addAll(records);
                }
                mergedRecords.sort(LogRecord::compareTo);
                
                mergedRecords.forEach(record -> System.out.println(record));
                showAlert("Success", "Log files were merged and printed to the console.", AlertType.INFORMATION);
                // display fxml file
                loadFXMLPage(mergedRecords);

            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error", "An error occurred while processing the log files.", AlertType.ERROR);
            }
        });

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(instructionLabel, pathInput, submitButton);

        Scene scene = new Scene(layout, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Log File Merger");
        primaryStage.show();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadFXMLPage(List<LogRecord> mergedRecords) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Page1.fxml"));
            Parent root = loader.load(); // load fxml

            Page1Controller controller = loader.getController();
            controller.setLogRecords(mergedRecords); // merged records
            controller.setAllLogRecords(mergedRecords);
            controller.setPrimaryStage(primaryStage);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the FXML file.", AlertType.ERROR);
        }
    }
}