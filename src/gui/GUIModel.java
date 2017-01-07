package gui;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.POMPOM;
import utils.Item;
import utils.ListClassifier;
import utils.Settings;
/**
 * @@author Jorel
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
