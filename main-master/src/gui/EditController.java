package gui;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

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
 * @@author A0126375A
 *
 */
public class EditController implements Initializable {

	// Pane items in Edit Controller
	@FXML
	TextField editTitle;
	@FXML
	DatePicker editStartDate;
	@FXML
	ComboBox<String> editStartHour;
	@FXML
	ComboBox<String> editStartMin;
	@FXML
	TextField editLabel;
	@FXML
	DatePicker editEndDate;
	@FXML
	ComboBox<String> editEndHour;
	@FXML
	ComboBox<String> editEndMin;
	@FXML
	ComboBox<String> editPriority;
	@FXML
	Button editSave;
	@FXML
	Button editCancel;
	

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

	// setting priority list
	ObservableList<String> options = FXCollections.observableArrayList("High", "Medium", "Low");
	// setting item type list
	ObservableList<String> itemType = FXCollections.observableArrayList("Task", "Event");
	// setting minutes list
	ObservableList<String> minutes = FXCollections.observableArrayList("00", "01", "02", "03", "04", "05", "06", "07",
			"08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
			"26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43",
			"44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59");
	// setting hours list
	ObservableList<String> hours = FXCollections.observableArrayList("00", "01", "02", "03", "04", "05", "06", "07",
			"08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");

	private Item item;

	/**
	 * This operation is to initialize Edit POPUP
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assert editTitle != null : "fx:id=\"editTitle\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editStartDate != null : "fx:id=\"editStartDate\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editStartHour != null : "fx:id=\"editStartHour\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editStartMin != null : "fx:id=\"editStartMin\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editEndDate != null : "fx:id=\"editEndDate\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editEndHour != null : "fx:id=\"editEndHour\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editEndMin != null : "fx:id=\"editEndMin\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editLabel != null : "fx:id=\"editLabel\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editPriority != null : "fx:id=\"editPriority\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editCancel != null : "fx:id=\"editCancel\" was not injected: check your FXML file 'Edit.fxml'.";
		assert editSave != null : "fx:id=\"editSave\" was not injected: check your FXML file 'Edit.fxml'.";

		editPriority.setItems(options);
		
		
		editStartHour.setItems(hours);
		editStartHour.setValue("00");
		editStartMin.setItems(minutes);
		editStartMin.setValue("00");

		editEndHour.setItems(hours);
		editEndHour.setValue("00");
		editEndMin.setItems(minutes);
		editEndMin.setValue("00");
	}

	/**
	 * This operation is to set Dialog Stage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * This operation is to get Item (Task or Event) details and place it in the
	 * respective fields
	 */
	public void setItem(Item item) {
		this.item = item;
		editTitle.setText(item.getTitle());
		editLabel.setText(item.getLabel());
		editPriority.setValue(item.getPriority());
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
			editStartDate.setValue(LocalDate.of(year, month + 1, day));

			editStartHour.setValue(Integer.toString(hour));
			editStartMin.setValue(Integer.toString(min));
		}
		Date retrieveEDate = item.getEndDate();
		if (retrieveEDate != null) {
			cal.setTime(retrieveEDate);
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			hour = cal.get(Calendar.HOUR_OF_DAY);
			min = cal.get(Calendar.MINUTE);
			editEndDate.setValue(LocalDate.of(year, month + 1, day));

			editEndHour.setValue(Integer.toString(hour));
			editEndMin.setValue(Integer.toString(min));
		}
	}

	/**
	 * This operation is to get user input
	 */
	private void getData() {
		title = editTitle.getText();
		startDate = editStartDate.getValue();
		startHour = editStartHour.getValue();
		startMin = editStartMin.getValue();
		endDate = editEndDate.getValue();
		endHour = editEndHour.getValue();
		endMin = editEndMin.getValue();
		label = editLabel.getText();
		priority = editPriority.getValue();
	}

	/**
	 * This operation is to format Start Date and Time to proper format for add
	 * command
	 * 
	 * @return Date sDate
	 * @throws ParseException
	 */
	private Date formatStartDateTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		Calendar startCal = Calendar.getInstance();
		Date sDate = null;
		if (startDate != null) {

			startDate.getYear();
			startCal.set(startDate.getYear(), startDate.getMonthValue() - 1, startDate.getDayOfMonth());
			Date sd = startCal.getTime();
			String sDateTime = sdf.format(sd) + " " + startHour + ":" + startMin;
			sDate = dateFormat.parse(sDateTime);

		}
		return sDate;
	}

	/**
	 * This operation is to format End Date and Time to proper format for add
	 * command
	 * 
	 * @return Date eDate
	 * @throws ParseException
	 */
	private Date formateEndDateTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		// FOr end date
		Date eDate = null;
		Calendar endCal = Calendar.getInstance();
		if (endDate != null) {
			endCal.set(endDate.getYear(), endDate.getMonthValue() - 1, endDate.getDayOfMonth());
			Date ed = endCal.getTime();
			String eDateTime = sdf.format(ed) + " " + endHour + ":" + endMin;
			eDate = dateFormat.parse(eDateTime);
		}
		return eDate;
	}

	/**
	 * This operation is to check for end date and start date if type is event
	 * 
	 * @param input
	 *            is the start date and end date
	 */
	private void checkEventValidDate(Date sDate, Date eDate) {
		if (type == "Event") {
			if (eDate == null || sDate == null) {
				String msg = "Invalid input: EVENT NEEDS START AND END DATE!";
				Timeline timeline = new Timeline();
				timeline.getKeyFrames().add(
						new KeyFrame(Duration.seconds(0), new KeyValue(mainControl.returnMsg.textProperty(), msg)));
				timeline.getKeyFrames().add(
						new KeyFrame(Duration.seconds(2), new KeyValue(mainControl.returnMsg.textProperty(), " ")));
				timeline.play();
				return;
			}

		}
	}

	/**
	 * This operation is to execute the edit commands
	 * 
	 * @param input
	 *            is the start date and end date
	 */
	private void editExecution(Date sDate, Date eDate) {
		// Edit the respective fields
		EditCommand editTitle = new EditCommand(taskId, "title", title);
		EditCommand editPriority = new EditCommand(taskId, "priority", priority);
		EditCommand editLabel = new EditCommand(taskId, "label", label);
		EditCommand editStartDate = new EditCommand(taskId, "start date", sDate);
		EditCommand editEndDate = new EditCommand(taskId, "end date", eDate);

		POMPOM.executeCommand(editTitle);
		POMPOM.executeCommand(editPriority);
		POMPOM.executeCommand(editLabel);
		POMPOM.executeCommand(editStartDate);
		POMPOM.executeCommand(editEndDate);
	}

	/**
	 * This operation is to handle actions upon on click save button on the
	 * edit.fxml
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	@FXML
	private void handleSave() throws IOException, ParseException {
		getData();
		Date sDate = formatStartDateTime();
		Date eDate = formateEndDateTime();
		checkEventValidDate(sDate, eDate);
		editExecution(sDate, eDate);
		POMPOM.getStorage().saveStorage();

		mainControl.setNotificationLabels();

		mainControl.switchToTab(POMPOM.getCurrentTab());
		dialogStage.close();
	}

	/**
	 * This operation is to handle cancel button action on the edit.fxml
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * This operation is used to return main controller
	 * 
	 * @return mainControl
	 */
	public MainController getMainControl() {
		return mainControl;
	}

	/**
	 * This operation is used to set main controller
	 */
	public void setMainControl(MainController mainControl) {
		this.mainControl = mainControl;
	}

}
