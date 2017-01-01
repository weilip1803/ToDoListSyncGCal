package gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.POMPOM;
import utils.Item;
import utils.ListClassifier;
import utils.Settings;

import command.DelCommand;

/**
 * @@author A0126375A
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

	// Colour String variables
	public static final String DEFAULT_LABEL_COLOR = "#7d8993";
	public static final String DEFAULT_HIGHLIGHT_COLOR = "#ffffff";
	public static final String CSS_STYLE_TEXT = "-fx-text-fill: #";

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

	/**
	 * General shortcuts
	 * 
	 * @@author A0126375A
	 */

	private final EventHandler<KeyEvent> shortcutHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent ke) {
			commonShortCut(ke);
			if (GOTO_COMMANDLINE_SHORTCUT.match(ke)) {
				inputCommand.requestFocus();
			} else if (GOTO_TABLE_SHORTCUT.match(ke)
					|| GOTO_TABLE_SHORTCUT_2.match(ke)) {

				table.requestFocus();

				table.getSelectionModel().select(0);
				table.getFocusModel().focus(0);
				return;
			} else if (NEW_TASK_SHORTCUT.match(ke)) {
				main.newDialog(currentMainController);
			} else if (EDIT_TASK_SHORTCUT.match(ke)) {
				Item item = table.getSelectionModel().getSelectedItem();
				if (item == null) {
					return;
				}
				main.editDialog(item, currentMainController);
			} else {
				return;
			}
		}
	};
	/**
	 * Short cut when tableview is focused
	 * 
	 * @@author A0126375A
	 *
	 */

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

	/**
	 * Shortcut for the inputFields
	 * 
	 * @@author A0126375A
	 *
	 */
	private final EventHandler<KeyEvent> inputFieldShortcut = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent ke) {
			commonShortCut(ke);

			if (GOTO_TABLE_SHORTCUT.match(ke)
					|| GOTO_TABLE_SHORTCUT_2.match(ke)) {

				table.requestFocus();

				table.getSelectionModel().select(0);
				table.getFocusModel().focus(0);
				return;
			}

		}
	};

	public ObservableList<Item> getDisplayList() {
		return displayList;
	}

	public void setDisplayList(ObservableList<Item> displayList) {
		this.displayList = displayList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 * 
	 * Initializes the resorces and configure the views when program is started
	 * 
	 * @@author A0126375A
	 */
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
		checkBox.setSortable(false);
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

	/**
	 * Delete the checked items when delete button is clicked
	 * 
	 * @@author A0126375A
	 * @throws IOException
	 */
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
		selectAll.selectedProperty().set(false);
		;
		switchToTab(POMPOM.getCurrentTab().toLowerCase());
		setNotificationLabels();
	}

	/**
	 * Lauches the new edit task dialog when new button is clicked
	 * 
	 * @@author A0126375A
	 * @param event
	 */
	@FXML
	private void editItem() throws IOException {
		Item item = table.getSelectionModel().getSelectedItem();
		main.editDialog(item, this);
	}

	/**
	 * Enable the edit button to be clikable when item on tableview is
	 * highlighted
	 * 
	 * @@author A0126375A
	 * @param event
	 */
	@FXML
	public void enableEditBtn(MouseEvent event) {
		if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
			editTask.setDisable(false);
		}
	}

	/**
	 * Lauches the new task dialog when new button is clicked
	 * 
	 * @@author A0126375A
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void newTaskFired(ActionEvent event) throws IOException {
		main.newDialog(this);
	}

	/**
	 * Launches the help dialog on click at help icon
	 * 
	 * @param event
	 * @@author A0126375A
	 */
	@FXML
	public void helpFired(ActionEvent event) {
		main.helpDialog();
	}

	/**
	 * Handles the event when the settings label is clicked It refreshes and
	 * initializes settings view.
	 * 
	 * @@author A0126375A
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void changeToSettings(ActionEvent event) throws IOException {

		highLightLabel(settingLbl);
		content.getChildren().clear();
		Node node = (Node) FXMLLoader.load(getClass().getResource(
				"Settings.fxml"));
		content.getChildren().setAll(node);
	}

	/**
	 * Handles the event when the dashboard label is clicked It refreshes and
	 * initializes dashboard view.
	 * 
	 * @@author A0126375A
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void changeToDashboard(ActionEvent event) throws IOException {
		highLightLabel(dashboardLbl);
		Node node = (Node) FXMLLoader.load(getClass()
				.getResource("POMPOM.fxml"));
		mainPane.getChildren().setAll(node);

	}

	/**
	 * Action that changes the display content list to high priority items
	 * 
	 * @@author A0121628L
	 * @param event
	 */
	@FXML
	public void changeToHighPriority(ActionEvent event) {

		highLightLabel(highPriorityLbl);
		// Filter the list and configure the table
		filterList("high");

	}

	/**
	 * Action that changes the display content list to medium priority items
	 * 
	 * @@author A0121628L
	 * @param event
	 */
	@FXML
	public void changeToMediumPriority(ActionEvent event) {
		highLightLabel(mediumPriorityLbl);
		// Filter the list and configure the table
		filterList("medium");
	}

	/**
	 * Action that changes the display content list to low priority items
	 * 
	 * @@author A0121628L
	 * @param event
	 */
	@FXML
	public void changeToLowPriority(ActionEvent event) {
		highLightLabel(lowPriorityLbl);
		// Filter the list and configure the table
		filterList("low");
	}

	/**
	 * The method selects the specified
	 * 
	 * @@author A0121628L
	 * @param inputTab
	 *            // Name of input tab
	 */
	public void switchToTab(String inputTab) {
		String tabName = inputTab.toLowerCase();
		SingleSelectionModel<Tab> selectionModel = tabViews.getSelectionModel();

		if (tabName.equals(POMPOM.LABEL_TASK.toLowerCase())) {
			selectionModel.select(taskTab);
			taskTabAction();
		} else if (tabName.equals(POMPOM.LABEL_COMPLETED_TASK.toLowerCase())) {
			selectionModel.select(completedTaskTab);
			completedTaskTabAction();
		} else if (tabName.equals(POMPOM.LABEL_EVENT.toLowerCase())) {
			selectionModel.select(eventTab);
			eventTabAction();
		} else if (tabName.equals(POMPOM.LABEL_COMPLETED_EVENT.toLowerCase())) {
			selectionModel.select(completedEventTab);
			completedEventTabAction();
		} else if (tabName.equals(POMPOM.LABEL_SEARCH.toLowerCase())) {
			selectionModel.select(searchTab);
			searchTabAction();
		}
	}

	/**
	 * Update Gui tabview and Displaylist view on this action
	 * 
	 * @@author A0121628L
	 * 
	 */
	@FXML
	public void taskTabAction() {
		if (!initialized)
			return;
		GUIModel.update();
		displayList = GUIModel.getTaskList();
		configureTable();
		POMPOM.setCurrentTab(POMPOM.LABEL_TASK);
	}

	/**
	 * Update Gui tabview and Displaylist view on this action
	 * 
	 * @@author A0121628L
	 * 
	 */
	@FXML
	public void completedTaskTabAction() {
		GUIModel.update();
		displayList = GUIModel.getTaskDoneList();
		configureTable();
		POMPOM.setCurrentTab(POMPOM.LABEL_COMPLETED_TASK);
	}

	/**
	 * Update Gui tabview and Displaylist view on this action
	 * 
	 * @@author A0121628L
	 */
	@FXML
	public void eventTabAction() {
		GUIModel.update();
		displayList = GUIModel.getEventList();
		configureTable();
		POMPOM.setCurrentTab(POMPOM.LABEL_EVENT);
	}

	/**
	 * Update Gui tabview and Displaylist view on this action
	 * 
	 * @@author A0121628L
	 */
	@FXML
	public void completedEventTabAction() {
		GUIModel.update();
		displayList = GUIModel.getEventDoneList();
		configureTable();
		POMPOM.setCurrentTab(POMPOM.LABEL_COMPLETED_EVENT);
	}

	/**
	 * Update Gui tabview and Displaylist view on this action
	 * 
	 * @@author A0121628L
	 */
	public void searchTabAction() {
		displayList = GUIModel.getSearchList();

		configureTable();
		POMPOM.setCurrentTab(POMPOM.LABEL_SEARCH);
	}

	/**
	 * @@author A0121628L
	 * @param event
	 *            --Button clicked is the event
	 * @throws IOException
	 */
	public void enterCommandFired(ActionEvent event) throws IOException {
		// Clear input string
		String input = inputCommand.getText();
		inputCommand.clear();
		// Execute command
		String msg = pompom.execute(input);
		POMPOM.getStorage().saveStorage();
		// Update Gui
		lanuchHelp(POMPOM.showHelp);
		displayReturnMessage(msg);
		configureTable();
		setNotificationLabels();
		inputCommand.setPromptText("Command:");
		switchToTab(POMPOM.getCurrentTab().toLowerCase());
		selectRow(input);
	}

	/**
	 * Method to handle the enter key pressed in input command
	 * 
	 * @@author A0121628L
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void enterCommandKey(KeyEvent event) throws IOException {
		if (event.getCode().equals(KeyCode.ENTER)) {
			// clear input string
			String input = inputCommand.getText();
			// Execute command
			String msg = pompom.execute(input);
			POMPOM.getStorage().saveStorage();

			// Update GUI
			displayReturnMessage(msg);
			configureTable();
			setNotificationLabels();

			lanuchHelp(POMPOM.showHelp);
			inputCommand.clear();
			inputCommand.setPromptText("Command:");
			switchToTab(POMPOM.getCurrentTab().toLowerCase());
			selectRow(input);
		}

	}

	/*********************************** Helper methods **********************************/

	/**
	 * Method to configure the buttons functionality(Clickable a not at the
	 * start) Edit is not clickable cause it requires user to highlight item.
	 * 
	 * @@author A0121628L
	 */
	private void configureButtons() {
		if (newTask != null) {
			newTask.setDisable(false);
		}
		if (editTask != null) {
			editTask.setDisable(true);
		}
		if (deleteTask != null) {
			deleteTask.setDisable(false);
		}
		if (enterCommand != null) {
			enterCommand.setDisable(false);
		}
	}

	/**
	 * Configure the table view with the latest and correct information
	 * 
	 * @@author A0121628L
	 */
	void configureTable() {
		// To tick checkbox
		table.setEditable(true);
		taskID.setCellValueFactory(new PropertyValueFactory<Item, Number>("id"));
		taskName.setCellValueFactory(new PropertyValueFactory<Item, String>(
				"title"));

		// Checkbox init
		checkBox.setGraphic(selectAll);
		checkBox.setCellFactory(CheckBoxTableCell.forTableColumn(checkBox));
		checkBox.setCellValueFactory(c -> c.getValue().checkedProperty());

		taskStartDateTime
				.setCellValueFactory(new PropertyValueFactory<Item, String>(
						"sd"));

		taskEndDateTime
				.setCellValueFactory(new PropertyValueFactory<Item, String>(
						"ed"));
		taskLabel.setCellValueFactory(new PropertyValueFactory<Item, String>(
				"label"));
		taskPriority
				.setCellValueFactory(new PropertyValueFactory<Item, String>(
						"priority"));
		taskStatus.setCellValueFactory(new PropertyValueFactory<Item, String>(
				"status"));

		GUIModel.update();

		selectAllCheckBox(selectAll);
		// Populate data

		table.setItems(displayList);
		table.refresh();

	}

	/**
	 * Highlight the item which has just been added or modified
	 * 
	 * @@author A0126375A
	 * @param input
	 */
	public void selectRow(String input) {
		int i = input.indexOf(' ');
		if (i < 0) {
			return;
		}
		String command = input.substring(0, i);
		String restOfAction = input.substring(i + 1);
		if(table.getItems() == null){
			return;
		}
		int rowNo = table.getItems().size();
		if (command.equals("add") || command.equals("event")) {
			table.getSelectionModel().select(rowNo - 1);
			table.scrollTo(rowNo - 1);

		}
		if (command.equals("edit")) {
			int z;
			int j = restOfAction.indexOf(' ');
			if (j < 0) {
				return;
			}
			int itemNo = 0;
			try {
				itemNo = Integer.parseInt(restOfAction.substring(0, j));
			} catch (Exception e) {
				// TODO: handle exception
			}

			for (z = 0; z < rowNo - 1; z++) {
				int searchItemNo = taskID.getCellData(z).intValue();
				if (itemNo == searchItemNo) {
					break;
				}
			}
			table.getSelectionModel().select(z);
			table.scrollTo(z);
		}

	}

	/**
	 * Method to help to select all checkbox in gui
	 * 
	 * @@author A0121628L
	 */
	public void selectAllCheckBox(CheckBox cb) {
		cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov,
					Boolean old_val, Boolean new_val) {

				int rows = table.getItems().size();
				for (int i = 0; i < rows; i++) {
					boolean preVal = displayList.get(i).getChecked();

					displayList.get(i).setChecked(!preVal);

				}
			}
		});
	}

	/**
	 * @@author A0121628L
	 * @param color
	 */
	private void setBackgroundColor(String color) {
		BackgroundFill myBF = new BackgroundFill(Color.valueOf(color),
				CornerRadii.EMPTY, Insets.EMPTY);
		content.setBackground(new Background(myBF));
	}

	/**
	 * Set the notification numbers in the labels Eg. Number of task today
	 * 
	 * @@author A0121628L
	 */
	public void setNotificationLabels() {
		GUIModel.update();
		taskNo.setText(ListClassifier.getTodayTask(POMPOM.getStorage()
				.getTaskList()));
		overdueNo.setText(ListClassifier.getOverdueTask(POMPOM.getStorage()
				.getTaskList()));
		eventsNo.setText(ListClassifier.getTodayEvent(POMPOM.getStorage()
				.getTaskList()));
	}

	/**
	 * Displays Notification Message
	 * 
	 * @@author A0121628L
	 */
	public void displayReturnMessage(String message) {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(0), new KeyValue(returnMsg
						.textProperty(), message)));
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(4), new KeyValue(returnMsg
						.textProperty(), " ")));
		timeline.play();
	}

	/**
	 * HighLight the clicked label
	 * 
	 * @@author A0121628L
	 * @param label
	 */
	public void highLightLabel(Label label) {
		dashboardLbl.setTextFill(Color.web(DEFAULT_LABEL_COLOR));
		settingLbl.setTextFill(Color.web(DEFAULT_LABEL_COLOR));
		highPriorityLbl.setTextFill(Color.web(DEFAULT_LABEL_COLOR));
		mediumPriorityLbl.setTextFill(Color.web(DEFAULT_LABEL_COLOR));
		lowPriorityLbl.setTextFill(Color.web(DEFAULT_LABEL_COLOR));

		label.setTextFill(Color.web(DEFAULT_HIGHLIGHT_COLOR));
	}

	/**
	 * Set the colours gotten from the settings text file
	 * 
	 * @@author A0121628L
	 */
	public void setSettingsColours() {
		Settings setting = GUIModel.getSettings();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setBackgroundColor(setting.getBackgroundColour());
				returnMsg.setStyle(CSS_STYLE_TEXT
						+ setting.getReturnMsgColour());
				inputCommand.setStyle(CSS_STYLE_TEXT
						+ setting.getInputTxtColour());

			}
		});
	}

	/**
	 * As method name.
	 * 
	 * @@author A0121628L
	 */
	private void initializeSceneShortcuts() {
		Scene scene = mainPane.getScene();
		scene.setOnKeyPressed(shortcutHandler);
		inputCommand.addEventHandler(KeyEvent.KEY_PRESSED, inputFieldShortcut);
		table.addEventHandler(KeyEvent.KEY_PRESSED, tableShortcut);

	}

	/**
	 * Common shortcut command that shares across all items in the view
	 * 
	 * @@author A0121628L
	 * @param ke
	 *            Keyevent
	 * 
	 */
	public void commonShortCut(KeyEvent ke) {
		if (UNDO_SHORTCUT.match(ke)) {
			String msg = pompom.execute(UNDO_COMMAND_STRING);
			switchToTab(POMPOM.getCurrentTab().toLowerCase());
			displayReturnMessage(msg);
			try {
				POMPOM.getStorage().saveStorage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			configureTable();
		} else if (SWITCH_TAB_SHORTCUT.match(ke)) {
			hotkeySwitchTab();
		} else if (DELETE_SHORTCUT.match(ke)) {
			Item item = table.getSelectionModel().getSelectedItem();
			if (item != null) {
				pompom.execute("delete " + item.getId());
				configureTable();
				try {
					POMPOM.getStorage().saveStorage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				switchToTab(GUIModel.getCurrentTab());
				return;
			}

		} else if (NO_FILTER_SHORTCUT.match(ke)) {
			switchToTab(GUIModel.getCurrentTab());
			highLightLabel(dashboardLbl);

		} else if (LOW_FILTER_SHORTCUT.match(ke)) {
			switchToTab(GUIModel.getCurrentTab());
			filterList("low");
			highLightLabel(lowPriorityLbl);
		} else if (MEDIUM_FILTER_SHORTCUT.match(ke)) {
			switchToTab(GUIModel.getCurrentTab());
			filterList("medium");
			highLightLabel(mediumPriorityLbl);
		} else if (HIGH_FILTER_SHORTCUT.match(ke)) {
			switchToTab(GUIModel.getCurrentTab());
			filterList("high");
			highLightLabel(highPriorityLbl);
		}
	}

	/**
	 * Hotkey to switch to next tab
	 *
	 * @@author A0121628L
	 */
	public void hotkeySwitchTab() {

		if (GUIModel.getCurrentTab().equals(POMPOM.LABEL_TASK)) {
			switchToTab(POMPOM.LABEL_COMPLETED_TASK);
			GUIModel.setCurrentTab(POMPOM.LABEL_COMPLETED_TASK);
		} else if (GUIModel.getCurrentTab().equals(POMPOM.LABEL_COMPLETED_TASK)) {
			switchToTab(POMPOM.LABEL_EVENT);
			GUIModel.setCurrentTab(POMPOM.LABEL_EVENT);
		} else if (GUIModel.getCurrentTab().equals(POMPOM.LABEL_EVENT)) {
			switchToTab(POMPOM.LABEL_COMPLETED_EVENT);
			GUIModel.setCurrentTab(POMPOM.LABEL_COMPLETED_EVENT);

		} else if (GUIModel.getCurrentTab()
				.equals(POMPOM.LABEL_COMPLETED_EVENT)) {
			switchToTab(POMPOM.LABEL_SEARCH);
			GUIModel.setCurrentTab(POMPOM.LABEL_SEARCH);

		} else if (GUIModel.getCurrentTab().equals(POMPOM.LABEL_SEARCH)) {
			switchToTab(POMPOM.LABEL_TASK);
			GUIModel.setCurrentTab(POMPOM.LABEL_TASK);
		}
	}

	/**
	 * filter the list
	 * 
	 * @@author A0121628L
	 * @param priority
	 */
	public void filterList(String priority) {
		GUIModel.update();
		// Filter
		switchToTab(GUIModel.getCurrentTab());
		displayList = ListClassifier.getSpecifiedPrirorirty(displayList,
				priority);
		configureTable();
	}

	/**
	 * Format the date to this format to be keyed into the table
	 * 
	 * @@author A0121628L
	 * @param date
	 */
	public void formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date d = new Date();
		dateFormat.format(d);

	}

	/**
	 * Check whether help dialog is required to launch a not
	 * 
	 * @@author A0121628L
	 * @param launched
	 *            when true launch help
	 */
	public void lanuchHelp(boolean launched) {
		if (launched == true) {
			POMPOM.showHelp = false;
			main.helpDialog();
		} else {
			return;
		}
	}

}
