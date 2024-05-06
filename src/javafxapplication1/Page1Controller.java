package javafxapplication1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class Page1Controller {
	@FXML
	private TextFlow logTextFlow; 
	@FXML
	private TextField commandTextField;

	@FXML
	private Button filterButton;
	@FXML
	private Button resetButton;
    @FXML
    private Button backButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button loadButton;
    @FXML
    private ComboBox<String> sortComboBox;
    private Stage primaryStage;
	private List<LogRecord> allLogRecords = new ArrayList<>();
	private List<LogRecord> originalLogRecords;
	private List<LogRecord> filteredLogRecords = new ArrayList<>();
	private List<List<LogRecord>> history = new ArrayList<>();
	public void setAllLogRecords(List<LogRecord> records) {
	    allLogRecords = new ArrayList<>(records);
	    originalLogRecords = new ArrayList<>(allLogRecords);
	    filteredLogRecords = allLogRecords;
	    history.add(new ArrayList<>(records));
	    setLogRecords(records); // show all records
	    initialize();

	}
	public void initialize() {
		sortComboBox.getItems().clear();
	    sortComboBox.getItems().addAll("Date", "Time", "IPAddress", "Username", "Role", "URL", "Description", "Timestamp");
	    sortComboBox.getSelectionModel().select("Timestamp");
	}
	@FXML
	private void onSaveButtonAction(ActionEvent event) {
	    showSaveDialog(); 
	}

	@FXML
	private void onLoadButtonAction(ActionEvent event) {
	    showLoadDialog(); 
	}
	private void showSaveDialog() {
	    try {
	        Stage saveDialogStage = new Stage();
	        Label label = new Label("Enter file path:");
	        TextField filePathField = new TextField();
	        filePathField.setPromptText("Enter or paste the file path here");

	        Button saveButton = new Button("Save");
	        saveButton.setOnAction(event -> {
	            String filePath = filePathField.getText();
	            if (!filePath.isEmpty()) {
	                saveToFile(new File(filePath));
	            }
	            saveDialogStage.close();
	        });

	        VBox layout = new VBox(10);
	        layout.setAlignment(Pos.CENTER);
	        layout.setPadding(new Insets(10));
	        layout.getChildren().addAll(label, filePathField, saveButton);

	        Scene scene = new Scene(layout, 300, 150);
	        saveDialogStage.setScene(scene);
	        saveDialogStage.setTitle("Save File");
	        saveDialogStage.showAndWait();
	    } catch (Exception e) {
	        e.printStackTrace();  // Log the exception
	    }
	}
	private void showLoadDialog() {
	    // Create a new stage
	    Stage loadDialogStage = new Stage();

	    // Label
	    Label label = new Label("Enter file path to load:");

	    // Text field
	    TextField filePathField = new TextField();
	    filePathField.setPromptText("Enter or paste the file path here");

	    // Button
	    Button loadButton = new Button("Load");
	    loadButton.setOnAction(event -> {
	        String filePath = filePathField.getText();
	        if (!filePath.isEmpty()) {
	            loadFromFile(new File(filePath));
	        }
	        loadDialogStage.close();
	    });

	    // Layout
	    VBox layout = new VBox(10);
	    layout.setAlignment(Pos.CENTER);
	    layout.setPadding(new Insets(10));
	    layout.getChildren().addAll(label, filePathField, loadButton);

	    // Setting the scene
	    Scene scene = new Scene(layout, 300, 150);
	    loadDialogStage.setScene(scene);
	    loadDialogStage.setTitle("Load File");
	    loadDialogStage.showAndWait();
	}
	private void loadFromFile(File file) {
	    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
	        filteredLogRecords = (List<LogRecord>) in.readObject(); // Load filtered records
	        history = (List<List<LogRecord>>) in.readObject(); // Load history
	        setLogRecords(filteredLogRecords); // Update UI
	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	}

	private void saveToFile(File file) {
	    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
	        out.writeObject(new ArrayList<>(filteredLogRecords)); // Save current filtered records
	        out.writeObject(history); // Save the history
	    } catch (IOException e) {
	    }
	}
	@FXML
	private void onSortOrderChanged(ActionEvent event) {
	    String selectedField = sortComboBox.getValue();
	    Comparator<LogRecord> comparator;

	    switch (selectedField) {
	        case "Date":
	            comparator = Comparator.comparing(LogRecord::getDate);
	            break;
	        case "Time":
	            comparator = Comparator.comparing(LogRecord::getTime);
	            break;
	        case "IPAddress":
	            comparator = Comparator.comparing(LogRecord::getIPAddress);
	            break;
	        case "Username":
	            comparator = Comparator.comparing(record -> record.getUsername() == null ? "anonymous" : record.getUsername(), Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
	            break;
	        case "Role":
	            comparator = Comparator.comparing(record -> record.getRole() == null ? "NA" : record.getRole(), Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
	            break;
	        case "URL":
	            comparator = Comparator.comparing(LogRecord::getUrl);
	            break;
	        case "Description":
	            comparator = Comparator.comparing(LogRecord::getDescription, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
	            break;
	        case "Timestamp":
	            comparator = Comparator.comparingLong(LogRecord::getTimestamp);
	            break;
	        default:
	            throw new IllegalArgumentException("Unexpected value: " + selectedField);
	    }

	    // Sorting
	    filteredLogRecords.sort(comparator);
	    setLogRecords(filteredLogRecords);
	}
	@FXML
	private void onResetButtonAction(ActionEvent event) {

	    filteredLogRecords.clear();
	    filteredLogRecords.addAll(originalLogRecords);
	    history.add(new ArrayList<>(filteredLogRecords));
	    setLogRecords(originalLogRecords);
	}

	@FXML
	private void onFilterButtonAction(ActionEvent event) {
	    String command = commandTextField.getText();
	    applyFilter(command);
	    history.add(new ArrayList<>(filteredLogRecords));
	}
    @FXML
    private void onBackButtonAction(ActionEvent event) {
        if (history.size() > 1) { 
            history.remove(history.size() - 1); 
            filteredLogRecords = new ArrayList<>(history.get(history.size() - 1)); 
            setLogRecords(filteredLogRecords);
        }
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

	private void applyFilter(String command) {
	    String[] parts = command.split(" ");
	    String attribute = parts[0].trim();
	    String operation = parts.length > 1 ? parts[1].trim() : "";
	    String value = parts.length > 2 ? parts[2].trim() : ""; 

	    List<LogRecord> currentFilteredRecords = new ArrayList<>();
	    if (attribute.equalsIgnoreCase("Date") && (operation.equals("=") || operation.equals(">") || operation.equals("<"))) {

	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	        for (LogRecord record : (filteredLogRecords)) {
	            if (recordMatchesDate(record, attribute, value, operation, dateFormatter)) {
	                currentFilteredRecords.add(record);
	            }
	        }
	    } else if (attribute.equalsIgnoreCase("Timestamp") && (operation.equals("=") || operation.equals(">") || operation.equals("<"))) {

	        long valueLong = Long.parseLong(value);
	        for (LogRecord record : (filteredLogRecords)) {
	            if (recordMatchesTimestamp(record, valueLong, operation)) {
	                currentFilteredRecords.add(record);
	            }
	        }
	    } else if (command.toLowerCase().contains("contains")) {
	        value = value.replaceAll("^\"|\"$", ""); 
	        String[] containsParts = command.split("(?i)Contains");
	        attribute = containsParts[0].replaceAll("\\.", "").trim();
	        value = containsParts[1].trim().replaceAll("^\"|\"$", "");

	        for (LogRecord record : (filteredLogRecords)) {
	            if (recordMatches(record, attribute, value)) {
	                currentFilteredRecords.add(record);
	            }
	        }
	    }

	    filteredLogRecords.clear();
	    filteredLogRecords.addAll(currentFilteredRecords);
	    setLogRecords(currentFilteredRecords);
	}

	private boolean recordMatchesTimestamp(LogRecord record, long value, String operation) {
	    long recordTimestamp = record.getTimestamp(); 

	    switch (operation) {
	        case "=":
	            return recordTimestamp == value;
	        case ">":
	            return recordTimestamp > value;
	        case "<":
	            return recordTimestamp < value;
	        default:
	            return false;
	    }
	}
	

	private boolean recordMatchesDate(LogRecord record, String attribute, String value, String operation, DateTimeFormatter dateFormatter) {
	    if (!"Date".equals(attribute) || record.getDate() == null) {
	        return false;
	    }

	    LocalDate recordDate = LocalDate.parse(record.getDate(), dateFormatter);
	    LocalDate valueDate = LocalDate.parse(value.substring(1, value.length() - 1), dateFormatter);

	    switch (operation) {
	        case "=":
	            return recordDate.isEqual(valueDate);
	        case ">":
	            return recordDate.isAfter(valueDate);
	        case "<":
	            return recordDate.isBefore(valueDate);
	        default:
	            return false;
	    }
	}

	private boolean recordMatches(LogRecord record, String attribute, String value) {
	    switch (attribute) {
	        case "Date":
	            return record.getDate() != null && record.getDate().contains(value.substring(2, value.length() - 2));
	        case "Time":
	            return record.getTime() != null && record.getTime().contains(value.substring(2, value.length() - 2));
	        case "IPAddress":
	            return record.getIPAddress() != null && record.getIPAddress().contains(value.substring(2, value.length() - 2));
	        case "Username":
	        	if (record.getUsername() == null) {
	        		if ((value.substring(2, value.length() - 2)).equalsIgnoreCase("anonymous")) {
						return true;
					}else
						return false;
				}
	            return record.getUsername() != null && record.getUsername().contains(value.substring(2, value.length() - 2));
	        case "Role":
	            return record.getRole() != null && record.getRole().contains(value.substring(2, value.length() - 2));
	        case "URL":
	            return record.getUrl() != null && record.getUrl().contains(value.substring(2, value.length() - 2));
	        case "Description":
	            return record.getDescription() != null && record.getDescription().contains(value.substring(2, value.length() - 2));
	        default:
	            return false;
	    }
	}

	public void setLogRecords(List<LogRecord> records) {
	    Platform.runLater(() -> {
	        logTextFlow.getChildren().clear();
	        for (LogRecord record : records) {
	            Text text = new Text(record.toString() + "\n");
	            logTextFlow.getChildren().add(text);
	        }
	    });
	}
	
}