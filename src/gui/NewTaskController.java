package gui;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import command.AddCommand;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.POMPOM;
import gui.MainController;

/**
 * @@author Jorel
 *
 */
public class NewTaskController implements Initializable {

	@FXML
	TextField newTaskTitle;
	@FXML
	DatePicker newStartDate;
	@FXML
	ComboBox<String> newStartHour;
	@FXML
	ComboBox<String> newStartMin;
	@FXML
	TextField newLabel;
	@FXML
	DatePicker newEndDate;
	@FXML
	ComboBox<String> newEndHour;
	@FXML
	ComboBox<String> newEndMin;
	@FXML
	ComboBox<String> newPriority;
	@FXML
	Button newTaskSave;
	@FXML
	Button newTaskCancel;
	@FXML
	ComboBox<String> newType;

	private Stage dialogStage;
	MainController mainControl;

	public MainController getMainControl() {
		return mainControl;
	}

	public void setMainControl(MainController mainControl) {
		this.mainControl = mainControl;
	}

	POMPOM pompom = new POMPOM();
	String title;
	LocalDate startDate;
	String startHour;
	String startMin;
	LocalDate endDate;
	String endHour;
	String endMin;
	String label;
	String priority;
	String type;

	boolean okClicked;

	ObservableList<String> options = FXCollections.observableArrayList("High",
			"Medium", "Low");
	ObservableList<String> itemType = FXCollections.observableArrayList("Task",
			"Event");
	ObservableList<String> minutes = FXCollections.observableArrayList("00",
			"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
			"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
			"23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33",
			"34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44",
			"45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", 
			"56", "57", "58", "59");
	ObservableList<String> hours = FXCollections.observableArrayList("00",
			"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
			"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
			"23");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assert newTaskTitle != null : "fx:id=\"newTaskTitle\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert newStartDate != null : "fx:id=\"newDate\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert newStartHour != null : "fx:id=\"newStartHour\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert newStartMin != null : "fx:id=\"newStartMin\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert newLabel != null : "fx:id=\"newLabel\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert newPriority != null : "fx:id=\"newPriority\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert newTaskCancel != null : "fx:id=\"newTaskCancel\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert newTaskSave != null : "fx:id=\"newTaskSave\" was not injected: check your FXML file 'POMPOM.fxml'.";
		
		newPriority.setItems(options);
		newType.setItems(itemType);
		newType.setValue("Task");

		newStartHour.setItems(hours);
		newStartHour.setValue("00");
		newStartMin.setItems(minutes);
		newStartMin.setValue("00");

		newEndHour.setItems(hours);
		newEndHour.setValue("00");
		newEndMin.setItems(minutes);
		newEndMin.setValue("00");

	}

	public void setDialogStage(Stage dialogStage) {

		this.dialogStage = dialogStage;
	}

	@FXML
	private void handleSave() throws IOException, ParseException {
		title = newTaskTitle.getText();

		startDate = newStartDate.getValue();
		startHour = newStartHour.getValue();
		startMin = newStartMin.getValue();
		endDate = newEndDate.getValue();
		endHour = newEndHour.getValue();
		endMin = newEndMin.getValue();

		label = newLabel.getText();
		priority = newPriority.getValue();
		type = newType.getValue();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		Calendar startCal = Calendar.getInstance();
		Date sDate = null;
		// For start date
		if (startDate != null) {

			startDate.getYear();
			
			startCal.set(startDate.getYear(), startDate.getMonthValue() - 1,
					startDate.getDayOfMonth());
			Date sd = startCal.getTime();
			String sDateTime = sdf.format(sd) + " " + startHour + ":"
					+ startMin;
			sDate = dateFormat.parse(sDateTime);
 
		}
		// FOr end date
		Date eDate = null;
		Calendar endCal = Calendar.getInstance();
		if (endDate != null) {	
			endCal.set(endDate.getYear(), endDate.getMonthValue()  - 1,
					endDate.getDayOfMonth());
			Date ed = endCal.getTime();
			String eDateTime = sdf.format(ed) + " " + endHour + ":" + endMin;
			eDate = dateFormat.parse(eDateTime);

		}
		if (type == "Event") {
			if (eDate == null || sDate == null) {
				String msg = "Invalid inputs: EVENT NEEDS START AND END DATE!";
				Timeline timeline = new Timeline();
				timeline.getKeyFrames().add(
						new KeyFrame(Duration.seconds(0), new KeyValue(
								mainControl.returnMsg.textProperty(), msg)));
				timeline.getKeyFrames().add(
						new KeyFrame(Duration.seconds(2), new KeyValue(
								mainControl.returnMsg.textProperty(), " ")));
				timeline.play();
				return;
			}

		}
		AddCommand addCommand = new AddCommand(type, title, "", priority, "",
				label, sDate, eDate);
		POMPOM.executeCommand(addCommand);
		POMPOM.getStorage().saveStorage();

		
		// Refresh Table
		mainControl.switchToTab(POMPOM.getCurrentTab());
		mainControl.setNotificationLabels();
		okClicked = true;
		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	
	public boolean isOkClicked() {
		return okClicked;
	}

	public boolean checkDateValues() {
		return false;

	}

}
