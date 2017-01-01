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
 * @@author A0126375A
 *
 */

public class NewController implements Initializable {

	// Pane items in New Controller
	@FXML
	TextField newTitle;
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
	Button newSave;
	@FXML
	Button newCancel;
	@FXML
	ComboBox<String> newType;

	private Stage dialogStage;

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

	MainController mainControl;

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

	/**
	 * This operation is to initialize New POPUP
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assert newTitle != null : "fx:id=\"newTitle\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newStartDate != null : "fx:id=\"newStartDate\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newStartHour != null : "fx:id=\"newStartHour\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newStartMin != null : "fx:id=\"newStartMin\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newEndDate != null : "fx:id=\"newEndDate\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newEndHour != null : "fx:id=\"newEndHour\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newEndMin != null : "fx:id=\"newEndMin\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newLabel != null : "fx:id=\"newLabel\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newPriority != null : "fx:id=\"newPriority\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newCancel != null : "fx:id=\"newCancel\" was not injected: check your FXML file 'NewTask.fxml'.";
		assert newSave != null : "fx:id=\"newSave\" was not injected: check your FXML file 'NewTask.fxml'.";

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

	/**
	 * This operation is to set Dialog Stage
	 */
	public void setDialogStage(Stage dialogStage) {

		this.dialogStage = dialogStage;
	}

	/**
	 * This operation is to get user input
	 */
	private void getData() {
		title = newTitle.getText();
		startDate = newStartDate.getValue();
		startHour = newStartHour.getValue();
		startMin = newStartMin.getValue();
		endDate = newEndDate.getValue();
		endHour = newEndHour.getValue();
		endMin = newEndMin.getValue();
		label = newLabel.getText();
		priority = newPriority.getValue();
		type = newType.getValue();
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
		// For start date
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
		// For end date
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
	 * This operation is to check validity end date and start date respective to their type
	 * 
	 * @param input
	 *            is the start date and end date
	 */
	private boolean checkValidDate(Date sDate, Date eDate) {
		if (type == "Event") {
			if (eDate == null || sDate == null) {
				String msg = "Invalid input: EVENT NEEDS START AND END DATE!";
				displayMsg(msg);
				return false;
			}
			if(!checkProperDate(sDate,eDate)){
				String msg = "Invalid input: Start date must be before end date!";
				displayMsg(msg);
				return false;
			}
			return true;
		}
		if(type == "Task"){
			if (eDate == null || sDate == null) {
				return true;
			}
			if(!checkProperDate(sDate,eDate)){
				String msg = "Invalid input: Start date must be before end date!";
				displayMsg(msg);
				return false;
			}
			else{
				return true;
			}
			
		}
		return true;
	}
	
	/**
	 * Return true if start date is before end date
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	private boolean checkProperDate(Date sDate, Date eDate){
		if(sDate.compareTo(eDate) < 0){
			return true;
		}else{
			return false;
		}
		
	}
	/**
	 * To display message in main controller telling the user when input goes wrong
	 * @param msg
	 */
	public void displayMsg(String msg){
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(0), new KeyValue(mainControl.returnMsg.textProperty(), msg)));
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(2), new KeyValue(mainControl.returnMsg.textProperty(), " ")));
		timeline.play();
	}
	/**
	 * This operation is to handle actions upon on click save button on the
	 * new.fxml
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	@FXML
	private void handleSave() throws ParseException, IOException, InterruptedException {
		getData();
		Date sDate = formatStartDateTime();
		Date eDate = formateEndDateTime();
		//Make sure title is set.
		if(title == null || title.equals("")){
			String msg = "Invalid input: Title cannot be empty string.!";
			displayMsg(msg);
			return;
		}
		boolean shouldSave = checkValidDate(sDate, eDate);
		if (!shouldSave) {
			return;
		}
		AddCommand addCommand = new AddCommand(type, title, "", priority, "", label, sDate, eDate);
		POMPOM.executeCommand(addCommand);
		POMPOM.getStorage().saveStorage();

		// Refresh Table
		mainControl.switchToTab(POMPOM.getCurrentTab());
		mainControl.setNotificationLabels();
		okClicked = true;
		dialogStage.close();
	}

	/** 
	 * This operation is to handle cancel button action on the new.fxml
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * This operation is see if save is click
	 * 
	 * @return boolean okClicked
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * This operation is to checkDatevalues
	 * 
	 * @return boolean false
	 */
	public boolean checkDateValues() {
		return false;

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
