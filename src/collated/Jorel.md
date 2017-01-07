# Jorel
###### gui\EditTaskController.java
``` java
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
```
###### gui\GUIModel.java
``` java
 **
 */
public class GUIModel {
	ArrayList<Item> displayList;
	public static Settings settings;
	ObservableList<String> taskView = FXCollections.observableArrayList(); 
	static ObservableList<Item> tableContent;
	public static ObservableList<Item> taskList;
	public static ObservableList<Item> taskDoneList;
	public static ObservableList<Item> eventList;
	public static ObservableList<Item> eventDoneList;
	public static ObservableList<Item> searchList;
	public static String currentTab = "tasks";
	
	public static Settings getSettings() {
		return settings;
	}
	public static void setSettings(Settings setting) {
		settings = setting;
	}
	public static ObservableList<Item> getSearchList() {
		return searchList;
	}
	public static void setSearchList(ObservableList<Item> searchList) {
		GUIModel.searchList = searchList;
	}
	public static ObservableList<Item> getTaskList() {
		return taskList;
	}
	public static void setTaskList(ObservableList<Item> taskList) {
		GUIModel.taskList = taskList;
	}
	public static ObservableList<Item> getTaskDoneList() {
		return taskDoneList;
	}
	public static void setTaskDoneList(ObservableList<Item> taskDoneList) {
		GUIModel.taskDoneList = taskDoneList;
	}
	public static ObservableList<Item> getEventList() {
		return eventList;
	}
	public static void setEventList(ObservableList<Item> eventList) {
		GUIModel.eventList = eventList;
	}
	public static ObservableList<Item> getEventDoneList() {
		return eventDoneList;
	}
	public static void setEventDoneList(ObservableList<Item> eventDoneList) {
		GUIModel.eventDoneList = eventDoneList;
	}
	public static void setTableContent(ObservableList<Item> tableContent) {
		GUIModel.tableContent = tableContent;
	}
	public static void setCurrentTab(String type) {
			currentTab = type;
		
	}
	public static String getCurrentTab() {
		return currentTab;
	}
	public static ObservableList<Item> getTableContent() {
		return tableContent;
	}
	public ArrayList<Item> getDisplayList() {
		return displayList;
	}
	public void setDisplayList(ArrayList<Item> displayList) {
		this.displayList = displayList;
	}
	private static ObservableList<Item> makeObservable(ArrayList<Item> arrayList) {
		if(arrayList == null){
			return null;
		}
		return FXCollections.observableArrayList(arrayList);		
	}	
	public static void taskList(ArrayList<Item> newTaskList) {
		taskList = makeObservable(newTaskList);
	}	
	public static void taskDoneList(ArrayList<Item> newTaskDoneList) {
		taskDoneList = makeObservable(newTaskDoneList);
	}
	public static void eventList(ArrayList<Item> newEventList) {
		eventList = makeObservable(newEventList);
	}
	public static void eventDoneList(ArrayList<Item> newEventDoneList) {
		eventDoneList = makeObservable(newEventDoneList);
	}	
	public static void searchList(ArrayList<Item> newSearchList) {
		searchList = makeObservable(newSearchList);
	}
	public static void update(){
		currentTab = POMPOM.getCurrentTab();
		setSettings(POMPOM.getStorage().getSettings());
		ArrayList<Item> currentList = POMPOM.getStorage().getTaskList();
		taskList(ListClassifier.getTaskList(currentList));
		taskDoneList(ListClassifier.getDoneTaskList(currentList));
		eventList(ListClassifier.getEventList(currentList));
		eventDoneList(ListClassifier.getDoneEventList(currentList));
		searchList(POMPOM.getSearchList());

	}
}
```
###### gui\Main.java
``` java
 *
 */
public class Main extends Application {

	@FXML
	Pane content; 
	
	MainController mainController;
	public static void main(String[] args) {
		Application.launch(Main.class, args); 
	}

	@Override 
	public void start(Stage stage) throws Exception {
		getClass();
		Parent root = FXMLLoader.load(getClass().getResource("POMPOM.fxml"));
		stage.setTitle("POMPOM");
		Scene scene = new Scene(root);
		
		scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ESCAPE) { 
					stage.close(); 
				}
			}
		});
		stage.setScene(scene);		
		
		stage.show();
		
	}

	public Stage newTaskDialog(MainController mainControl) {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("NewTask.fxml"));
			Pane page = (Pane) loader.load();			
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("New Task");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			NewTaskController newTaskController = loader.getController();
			newTaskController.setDialogStage(dialogStage);
			newTaskController.setMainControl(mainControl);		
			
			dialogStage.showAndWait(); 
			return dialogStage;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Stage editTaskDialog(Item item, MainController mainControl) {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("EditTask.fxml"));
			Pane page = (Pane) loader.load();

			Stage dialogStage = new Stage(); 
			dialogStage.setTitle("Edit Task");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			EditTaskController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setItem(item);
			
			controller.setMainControl(mainControl);
			
			dialogStage.showAndWait();
			return dialogStage;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void helpDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("Help.fxml"));
			Pane page = (Pane) loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Help");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			Scene scene = new Scene(page);
			
			scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ESCAPE) { 
						dialogStage.close(); 
					}
				}
			});
			
			dialogStage.setScene(scene);
			dialogStage.showAndWait();			
	

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
```
###### gui\MainController.java
``` java
 *
 */

public class MainController implements Initializable {
	// Pane items in main display
	@FXML
	Pane mainContent;
	@FXML
	Pane content;
	@FXML
	Button settings;
	@FXML
	Button settingBtn;
	@FXML
	Button highPBtn;
	@FXML
	Button mediumPBtn;
	@FXML
	Button newTask;
	@FXML
	Button editTask;
	@FXML
	Button deleteTask;
	@FXML
	Button help;
	@FXML
	Button enterCommand;
	@FXML
	TableView<Item> table;
	@FXML
	TableColumn<Item, Number> taskID;
	@FXML
	TableColumn<Item, String> taskName;
	@FXML
	TableColumn<Item, Boolean> checkBox;

	@FXML
	TableColumn<Item, String> taskStartDateTime;
	@FXML
	TableColumn<Item, String> taskEndDateTime;
	@FXML
	TableColumn<Item, String> taskLabel;
	@FXML
	TableColumn<Item, String> taskPriority;
	@FXML
	TableColumn<Item, String> taskStatus;
	@FXML
	TextField inputCommand;
	@FXML
	Label returnMsg;
	@FXML
	Label label;
	@FXML
	Label priorityLabel;
	@FXML
	TabPane tabViews;
	@FXML
	private Tab taskTab;
	@FXML
	private Tab completedTaskTab;
	@FXML
	private Tab eventTab;
	@FXML
	private Tab completedEventTab;
	@FXML
	private Tab searchTab;

	// DASHBOARD ITEMS
	@FXML
	Label dashboardLbl;
	@FXML
	Label settingLbl;
	@FXML
	Label lowPriorityLbl;
	@FXML
	Label highPriorityLbl;
	@FXML
	Label mediumPriorityLbl;
	@FXML
	Button dashBoard;
	@FXML
	Label taskNo;
	@FXML
	Label overdueNo;
	@FXML
	Label eventsNo;
	@FXML
	Pane mainPane;
	// Content
	Stage stage;
	private Main main = new Main();
	private boolean initialized = false;
	ObservableList<Item> displayList;
	ObservableList<String> taskView = FXCollections.observableArrayList();
	POMPOM pompom;
	CheckBox selectAll = new CheckBox();
	MainController currentMainController = this;

	/** Shortcuts **/
	private static final String UNDO_COMMAND_STRING = "undo";
	private static final KeyCodeCombination SWITCH_TAB_SHORTCUT = new KeyCodeCombination(
			KeyCode.TAB, KeyCombination.CONTROL_DOWN);
	private static final KeyCodeCombination UNDO_SHORTCUT = new KeyCodeCombination(
			KeyCode.Z, KeyCombination.CONTROL_DOWN);
	private static final KeyCodeCombination GOTO_COMMANDLINE_SHORTCUT = new KeyCodeCombination(
			KeyCode.ENTER);

	private static final KeyCodeCombination GOTO_TABLE_SHORTCUT = new KeyCodeCombination(
			KeyCode.DOWN);

	private static final KeyCodeCombination GOTO_TABLE_SHORTCUT_2 = new KeyCodeCombination(
			KeyCode.UP);

	private static final KeyCodeCombination NEW_TASK_SHORTCUT = new KeyCodeCombination(
			KeyCode.N, KeyCombination.CONTROL_DOWN);	
	private static final KeyCodeCombination EDIT_TASK_SHORTCUT = new KeyCodeCombination(
			KeyCode.E, KeyCombination.CONTROL_DOWN);
	private static final KeyCodeCombination DELETE_SHORTCUT = new KeyCodeCombination(
			KeyCode.DELETE);
	
	private static final KeyCodeCombination NO_FILTER_SHORTCUT = new KeyCodeCombination(
			KeyCode.DIGIT1, KeyCombination.ALT_DOWN);
	private static final KeyCodeCombination HIGH_FILTER_SHORTCUT = new KeyCodeCombination(
			KeyCode.DIGIT2, KeyCombination.ALT_DOWN);
	private static final KeyCodeCombination MEDIUM_FILTER_SHORTCUT = new KeyCodeCombination(
			KeyCode.DIGIT3, KeyCombination.ALT_DOWN);
	private static final KeyCodeCombination LOW_FILTER_SHORTCUT = new KeyCodeCombination(
			KeyCode.DIGIT4, KeyCombination.ALT_DOWN);

	private final EventHandler<KeyEvent> shortcutHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent ke) {
			commonShortCut(ke);			
			if (GOTO_COMMANDLINE_SHORTCUT.match(ke)) {
				inputCommand.requestFocus();
			}
			
			if (GOTO_TABLE_SHORTCUT.match(ke)
					|| GOTO_TABLE_SHORTCUT_2.match(ke)) {

				table.requestFocus();

				table.getSelectionModel().select(0);
				table.getFocusModel().focus(0);
				System.out.println("WTTFFFF");
				return;
			}
			
			else if (NEW_TASK_SHORTCUT.match(ke)) {
				main.newTaskDialog(currentMainController);
			}
			else if (EDIT_TASK_SHORTCUT.match(ke)) {
				Item item = table.getSelectionModel().getSelectedItem();
				if (item == null) {
					return;
				}
				main.editTaskDialog(item, currentMainController);
			} 
			else {
				return;
			}
		}
	};
	private final EventHandler<KeyEvent> tableShortcut = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent ke) {
			
			commonShortCut(ke);
			if (GOTO_COMMANDLINE_SHORTCUT.match(ke)) {
				inputCommand.requestFocus();
			} else {
				return;
			}
		}
	};
	private final EventHandler<KeyEvent> inputFieldShortcut = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent ke) {
			commonShortCut(ke);

			if (GOTO_TABLE_SHORTCUT.match(ke)
					|| GOTO_TABLE_SHORTCUT_2.match(ke)) {

				table.requestFocus();

				table.getSelectionModel().select(0);
				table.getFocusModel().focus(0);
				System.out.println("WTTFFFF");
				return;
			}

		}
	};

	// String variables
	public static final String DEFAULT_LABEL_COLOR = "#7d8993";
	public static final String DEFAULT_HIGHLIGHT_COLOR = "#ffffff";
	public static final String CSS_STYLE_TEXT = "-fx-text-fill: #";

	public ObservableList<Item> getDisplayList() {
		return displayList;
	}

	public void setDisplayList(ObservableList<Item> displayList) {
		this.displayList = displayList;
	}

	public void formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date d = new Date();
		dateFormat.format(d);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assert newTask != null : "fx:id=\"newTask\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert editTask != null : "fx:id=\"editTask\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert deleteTask != null : "fx:id=\"deleteTask\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert enterCommand != null : "fx:id=\"enterCommand\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert checkBox != null : "fx:id=\"checkBox\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert taskID != null : "fx:id=\"taskID\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert taskName != null : "fx:id=\"taskName\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert taskStartDateTime != null : "fx:id=\"taskDateTime\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert taskEndDateTime != null : "fx:id=\"taskDateTime\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert taskLabel != null : "fx:id=\"taskLabel\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert taskPriority != null : "fx:id=\"taskPriority\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert taskStatus != null : "fx:id=\"taskStatus\" was not injected: check your FXML file 'POMPOM.fxml'.";
		assert inputCommand != null : "fx:id=\"inputCommand\" was not injected: check your FXML file 'POMPOM.fxml'.";

		// Initialize main logic class
		pompom = new POMPOM();
		GUIModel.update();

		// Initialize the GUI
		POMPOM.setCurrentTab(POMPOM.LABEL_TASK);
		displayList = GUIModel.getTaskList();
		configureButtons();
		configureTable();
		setNotificationLabels();

		// Set focus to command text striaght
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initializeSceneShortcuts();
				inputCommand.requestFocus();
			}
		});
		setSettingsColours();
		initialized = true;

	}

	@FXML
	private void deleteItems() throws IOException {
		int rows = table.getItems().size();
		for (int i = 0; i < rows; i++) {
			if (checkBox.getCellData(i) == true) {

				DelCommand delCommand = new DelCommand(Long.parseLong(taskID
						.getCellData(i).toString()));
				POMPOM.executeCommand(delCommand);
				configureTable();
				POMPOM.getStorage().saveStorage();
			}
		}
		selectAll.selectedProperty().set(false);;
		switchToTab(POMPOM.getCurrentTab().toLowerCase());
	}

	@FXML
	private void editItem() throws IOException {
		Item item = table.getSelectionModel().getSelectedItem();
		main.editTaskDialog(item, this);
	}

	@FXML
	public void enableEditBtn(MouseEvent event) {
		if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
			editTask.setDisable(false);
		}
	}

	@FXML
	public void newTaskFired(ActionEvent event) throws IOException {
		main.newTaskDialog(this);
	}

	@FXML
	public void helpFired(ActionEvent event) {
		main.helpDialog();
	}

	@FXML
	public void changeToSettings(ActionEvent event) throws IOException {

		highLightLabel(settingLbl);

		content.getChildren().clear();
		Node node = (Node) FXMLLoader.load(getClass().getResource(
				"Settings.fxml"));
		content.getChildren().setAll(node);
	}

	@FXML
	public void changeToDashboard(ActionEvent event) throws IOException {

		highLightLabel(dashboardLbl);

		Node node = (Node) FXMLLoader.load(getClass()
				.getResource("POMPOM.fxml"));
		mainPane.getChildren().setAll(node);

		// content.getChildren().setAll(node);

	}

	/**
```
###### gui\NewTaskController.java
``` java
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
```
###### gui\SettingsController.java
``` java
 **
 */
public class SettingsController implements Initializable {
	@FXML
	Button saveFile;
	@FXML
	Button selectFile;
	@FXML
	TextField storageLocationString;
	@FXML
	ColorPicker backgroundColour;
	@FXML
	ColorPicker displayMsgColor;
	@FXML
	ColorPicker commandTextColor;
	@FXML
	Pane mainPane;
	@FXML
	Label displayMsg;
	
	Settings currentSettings;
	public static final String RETURN_MSG = "Settings Saved";
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assert saveFile != null : "fx:id=\"saveFile\" was not injected: check your FXML file 'Settings.fxml'.";
		assert selectFile != null : "fx:id=\"selectFile\" was not injected: check your FXML file 'Settings.fxml'.";
		assert storageLocationString != null : "fx:id=\"storageLocationString\" was not injected: check your FXML file 'Settings.fxml'.";
		assert backgroundColour != null : "fx:id=\"backgroundColour\" was not injected: check your FXML file 'Settings.fxml'.";
		assert displayMsgColor != null : "fx:id=\"tabColour\" was not injected: check your FXML file 'Settings.fxml'.";
		
		init();
	}
	//Set the current display colours if not set to white
	public void init() {
		currentSettings = GUIModel.getSettings();
		storageLocationString.setText(currentSettings.getStoragePath());
		backgroundColour.setValue(Color.valueOf(currentSettings.getBackgroundColour())); 
		displayMsgColor.setValue(Color.valueOf(currentSettings.getReturnMsgColour()));
		commandTextColor.setValue(Color.valueOf(currentSettings.getInputTxtColour()));
	}
	public String getColorString(ColorPicker picker){
		String hex = picker.getValue().toString();
		String color = hex.substring(2, hex.length() -2);
		return color;
	} 
	
    public void clickSave(ActionEvent event) throws IOException {
    	String storageFilePath = storageLocationString.getText(); 
//    	storageLocationString.clear();
    	
    	saveSettings();    	
/*    	BackgroundFill myBF = new BackgroundFill(Color.valueOf(getColorString(backgroundColour)),
				CornerRadii.EMPTY, Insets.EMPTY);
		mainPane.setBackground(new Background(myBF));*/
		
    	displayMsg.setStyle(MainController.CSS_STYLE_TEXT + getColorString(displayMsgColor));
    	displayMsg.setText(RETURN_MSG);
    	
        POMPOM.saveSettings(storageFilePath);
    }  
	
    

	public void showSingleFileChooser() {
		FileChooser fileChooser = new FileChooser(); 
		File selectedPath = fileChooser.showOpenDialog(null);
		storageLocationString.setText(selectedPath.getPath());
	}
	public void saveSettings(){
    	currentSettings.setBackgroundColour(getColorString(backgroundColour));
    	currentSettings.setReturnMsgColour(getColorString(displayMsgColor));
    	currentSettings.setInputTxtColour(getColorString(commandTextColor));
	}

}
```
###### Test\TestSomething.java
``` java
 *
 */
public class TestSomething {
	/**
	 * This operation test return responds for correct input task add
	 */
	@Test
	public void test() {
		POMPOM pompom = new POMPOM();
		assertEquals("Task added", pompom.execute("add hello"));
	}
	
	/**
	 * This operation test return responds for invalid input
	 */
	@Test
	public void testTwo() {
		POMPOM pompom = new POMPOM();
		assertEquals("hello", pompom.execute("hello"));
	}

}
```
