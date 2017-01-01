package gui;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.POMPOM;
import utils.Item;
import utils.ListClassifier;
import utils.Settings;

/**
 * @@author A0126375A
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

	/**
	 * This operation is to get settings
	 */
	public static Settings getSettings() {
		return settings;
	}

	/**
	 * This operation is to set settings
	 * @param Settings setting
	 */
	public static void setSettings(Settings setting) {
		settings = setting;
	}

	/**
	 * This operation is to retrieve search list
	 */
	public static ObservableList<Item> getSearchList() {
		return searchList;
	}

	/**
	 * This operation is to place search results
	 */
	public static void setSearchList(ObservableList<Item> searchList) {
		GUIModel.searchList = searchList;
	}

	/**
	 * This operation is to retrieve task list
	 */
	public static ObservableList<Item> getTaskList() {
		return taskList;
	}

	/**
	 * This operation is to place task results
	 */
	public static void setTaskList(ObservableList<Item> taskList) {
		GUIModel.taskList = taskList;
	}

	/**
	 * This operation is to retrieve completed task list
	 */
	public static ObservableList<Item> getTaskDoneList() {
		return taskDoneList;
	}

	/**
	 * This operation is to place completed task results
	 */
	public static void setTaskDoneList(ObservableList<Item> taskDoneList) {
		GUIModel.taskDoneList = taskDoneList;
	}

	/**
	 * This operation is to retrieve event list
	 */
	public static ObservableList<Item> getEventList() {
		return eventList;
	}

	/**
	 * This operation is to place event results
	 */
	public static void setEventList(ObservableList<Item> eventList) {
		GUIModel.eventList = eventList;
	}

	/**
	 * This operation is to retrieve completed event list
	 */
	public static ObservableList<Item> getEventDoneList() {
		return eventDoneList;
	}

	/**
	 * This operation is to place completed event list
	 */
	public static void setEventDoneList(ObservableList<Item> eventDoneList) {
		GUIModel.eventDoneList = eventDoneList;
	}

	/**
	 * This operation is to place contents into table
	 */
	public static void setTableContent(ObservableList<Item> tableContent) {
		GUIModel.tableContent = tableContent;
	}

	/**
	 * This operation is to switch between tabs
	 * @param String type where type is the different kind of category
	 */
	public static void setCurrentTab(String type) {
		currentTab = type;
	}

	/**
	 * This operation is to get the current tab that is on view
	 */
	public static String getCurrentTab() {
		return currentTab;
	}

	/**
	 * This operation is to retrieve contents
	 */
	public static ObservableList<Item> getTableContent() {
		return tableContent;
	}

	/**
	 * This operation is to retrieve displayed list
	 */
	public ArrayList<Item> getDisplayList() {
		return displayList;
	}

	/**
	 * This operation is to place display list
	 */
	public void setDisplayList(ArrayList<Item> displayList) {
		this.displayList = displayList;
	}

	/**
	 * This operation is to cast array list to observable list
	 * @param arraylist for casting
	 */
	private static ObservableList<Item> makeObservable(ArrayList<Item> arrayList) {
		if (arrayList == null) {
			return null;
		}
		return FXCollections.observableArrayList(arrayList);
	}

	/**
	 * This operation is to cast task array list to task observable list
	 * @param arraylist for casting
	 */
	public static void taskList(ArrayList<Item> newTaskList) {
		taskList = makeObservable(newTaskList);
	}

	/**
	 * This operation is to cast completed task array list to completed task observable list
	 * @param arraylist for casting
	 */
	public static void taskDoneList(ArrayList<Item> newTaskDoneList) {
		taskDoneList = makeObservable(newTaskDoneList);
	}

	/**
	 * This operation is to cast event array list to event observable list
	 * @param arraylist for casting
	 */
	public static void eventList(ArrayList<Item> newEventList) {
		eventList = makeObservable(newEventList);
	}

	/**
	 * This operation is to cast completed event array list to completed event observable list
	 * @param arraylist for casting
	 */
	public static void eventDoneList(ArrayList<Item> newEventDoneList) {
		eventDoneList = makeObservable(newEventDoneList);
	}

	/**
	 * This operation is to cast search array list to search observable list
	 * @param arraylist for casting
	 */
	public static void searchList(ArrayList<Item> newSearchList) {
		searchList = makeObservable(newSearchList);
	}
	
	/**
	 * This operation is to update model
	 */
	public static void update() {
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
