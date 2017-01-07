package gui;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import command.AddCommand;
import command.EditCommand;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.POMPOM;
import utils.Item;
import gui.MainController;

/**
 * @@author Jorel
 *
 */
public class EditTaskController implements Initializable {

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
	Long taskId;

	int year;
	int month;
	int day;
	int hour;
	int min;
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

	private Item item;

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

	public void setItem(Item item) {
		this.item = item;
		newTaskTitle.setText(item.getTitle());
		newLabel.setText(item.getLabel());
		newPriority.setValue(item.getPriority());
		newType.setValue(item.getType());
		taskId = item.getId();
		Date retrieveSDate = item.getStartDate();
		Calendar cal = Calendar.getInstance();

		if (retrieveSDate != null) {
			cal.setTime(retrieveSDate);
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			hour = cal.get(Calendar.HOUR_OF_DAY);
			min = cal.get(Calendar.MINUTE);
			newStartDate.setValue(LocalDate.of(year, month + 1, day));

			newStartHour.setValue(Integer.toString(hour));
			newStartMin.setValue(Integer.toString(min));
		}
		Date retrieveEDate = item.getEndDate();
		if (retrieveEDate != null) { 
			cal.setTime(retrieveEDate);
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			hour = cal.get(Calendar.HOUR_OF_DAY);
			min = cal.get(Calendar.MINUTE);
			newEndDate.setValue(LocalDate.of(year, month + 1, day));
			
			newEndHour.setValue(Integer.toString(hour));
			newEndMin.setValue(Integer.toString(min)); 
		}
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
		Calendar sc = Calendar.getInstance();
		Date sDate = null;
		// For start date
		System.out.println("STARTDATE:" + startDate);
		if (startDate != null) {

			startDate.getYear();
			System.out.println(startDate.getYear());
			sc.set(startDate.getYear(), startDate.getMonthValue() - 1,
					startDate.getDayOfMonth());
			Date sd = sc.getTime();
			String sDateTime = sdf.format(sd) + " " + startHour + ":"
					+ startMin;
			sDate = dateFormat.parse(sDateTime);

		}
		// FOr end date
		Date eDate = null;
		if (endDate != null) {
			Calendar ec = Calendar.getInstance();
			ec.set(endDate.getYear(), endDate.getMonthValue() - 1,
					endDate.getDayOfMonth());
			Date ed = ec.getTime();
			String eDateTime = sdf.format(ed) + " " + endHour + ":" + endMin;
			eDate = dateFormat.parse(eDateTime);

		}
		if (type == "Event") {
			if (eDate == null || sDate == null) {
				String msg = "Invalid: EVENT NEEDS START AND END DATE!";
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
		
		

		// Edit the respective fields
		EditCommand editTitle = new EditCommand(taskId, "title", title);
		EditCommand editPriority = new EditCommand(taskId, "priority", priority);
		EditCommand editLabel = new EditCommand(taskId, "label", label);
		EditCommand editStatus = new EditCommand(taskId, "type", type);
		EditCommand editStartDate = new EditCommand(taskId, "start date", sDate);
		EditCommand editEndDate = new EditCommand(taskId, "end date", eDate);

		POMPOM.executeCommand(editTitle);
		POMPOM.executeCommand(editPriority);
		POMPOM.executeCommand(editLabel);
		POMPOM.executeCommand(editStatus);
		POMPOM.executeCommand(editStartDate);
		POMPOM.executeCommand(editEndDate);
		POMPOM.getStorage().saveStorage(); 

		mainControl.setNotificationLabels();
		
		mainControl.switchToTab(POMPOM.getCurrentTab());
		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	public MainController getMainControl() {
		return mainControl;
	}

	public void setMainControl(MainController mainControl) {
		this.mainControl = mainControl;
	}

}
