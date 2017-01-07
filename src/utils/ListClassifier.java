package utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import main.POMPOM;
import javafx.collections.ObservableList;

/**
 * @@author A0121628L 
 * These static methods help to return filtered list.
 */
public class ListClassifier {

	
	private static boolean checkIsToday(Date input, Date sd) {
		if(sd == null){
			return false;
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(input);
		cal2.setTime(sd);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
			return sameDay;
	}

	public static String getTodayTask(ArrayList<Item> lst){
		Date currentDate = new Date();
		int counter = 0;
		for (int i = 0; i < lst.size(); i++) {
			Item currentTask = lst.get(i);
			if(!currentTask.getType().toLowerCase().equals("task")){
				continue;
			}
			if(currentTask.getStatus().equals("ongoing")){
				counter++;
				continue;
			}
			boolean todayCheck = checkIsToday(currentDate, currentTask.getStartDate());
			// Remove this line after proper init of task and events
			if (todayCheck){
				counter++;
			}
			
		}
		return Integer.toString(counter);
	}

	public static String getTodayEvent(ArrayList<Item> lst) {
		Date currentDate = new Date();
		int counter = 0;
		for (int i = 0; i < lst.size(); i++) {
			Item currentTask = lst.get(i);
			if(!currentTask.getType().toLowerCase().equals("event")){
				continue;
			}
			if(currentTask.getStatus().equals("ongoing")){
				counter++;
				continue;
			}
			boolean todayCheck = checkIsToday(currentDate, currentTask.getStartDate());
			// Remove this line after proper init of task and events
			if (todayCheck){
				counter++;
			}
			
		}
		return Integer.toString(counter);

	}

	public static String getOverdueTask(ArrayList<Item> lst) {
		int counter = 0;
		for (int i = 0; i < lst.size(); i++) {
			Item currentTask = lst.get(i);
			if(currentTask.getType().toLowerCase().equals("task")
					&& currentTask.getStatus().equals("Overdue")){
				counter++;
			}
		
			
		}
		return Integer.toString(counter);

	}
	/**
	 * This method filter the given list and return the item list which contains
	 * task only or empty list
	 * 
	 * @param lst
	 * @return
	 */
	public static ArrayList<Item> getTaskList(ArrayList<Item> lst) {
		ArrayList<Item> result = new ArrayList<Item>();
		for (int i = 0; i < lst.size(); i++) {
			Item currentTask = lst.get(i);
			// Remove this line after proper init of task and event
			if (currentTask.getType() == null)
				continue;
			if (currentTask.getType().toLowerCase().equals("task")
					&& !currentTask.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
				result.add(currentTask);
			}
		}
		return result;
	}

	//
	/**
	 * This method filter the given list and return the item list which contains
	 * completed task only or empty list
	 * 
	 * @param lst
	 * @return
	 */
	public static ArrayList<Item> getDoneTaskList(ArrayList<Item> lst) {
		ArrayList<Item> result = new ArrayList<Item>();
		for (int i = 0; i < lst.size(); i++) {
			Item currentTask = lst.get(i);
			// Remove this line after proper init of task and events

			if (currentTask.getType() == null)
				continue;
			if (currentTask.getType().toLowerCase().equals("task")
					&& currentTask.getStatus().toLowerCase()
							.equals("completed")) {
				result.add(currentTask);
			}
		}
		return result;
	}

	//
	/**
	 * This method filter the given list and return the item list which contains
	 * event only or empty list
	 * 
	 * @param lst
	 * @return
	 */
	public static ArrayList<Item> getEventList(ArrayList<Item> lst) {
		ArrayList<Item> result = new ArrayList<Item>();
		for (int i = 0; i < lst.size(); i++) {
			Item currentTask = lst.get(i);
			if (currentTask.getType() == null)
				continue;
			if (currentTask.getType().equals("Event")
					&& !currentTask.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
				result.add(currentTask);
			}
		}
		return result;
	}

	/**
	 * This method filter the given list and return the item list which contains
	 * completed events only or empty list
	 * 
	 * @param lst
	 * @return
	 */
	public static ArrayList<Item> getDoneEventList(ArrayList<Item> lst) {
		ArrayList<Item> result = new ArrayList<Item>();
		for (int i = 0; i < lst.size(); i++) {
			Item currentTask = lst.get(i);
			if (currentTask.getType() == null) {
				continue;
			}
			if (currentTask.getType().equals("Event")
					&& currentTask.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
				result.add(currentTask);
			}
		}
		return result;
	}

	public static ObservableList<Item> getSpecifiedPrirorirty(ObservableList<Item> lst,
			String priority) {
		System.out.println("SIZE: "+lst.size());
		for (int i = 0; i < lst.size(); i++) {
			Item currentTask = lst.get(i);
			System.out.println("TASK NUMBER: " + i + "\n Priority: " + currentTask.getTitle());
			
			if (currentTask.getPriority() == null) {
				i--;
				lst.remove(currentTask);
				continue;
			}
			
			if (!currentTask.getPriority().toLowerCase().equals(priority)) {
				i--;
				lst.remove(currentTask);
			}
		}
		return lst;

	}

	// public ArrayList<Item> getPendingList() {
	// ArrayList<Item> result = new ArrayList<Item>();
	// for (int i = 0; i < taskArrayList.size(); i++) {
	// Item currentTask = taskArrayList.get(i);
	// if (currentTask.getStatus().toLowerCase().equals("pending")) {
	// result.add(currentTask);
	// }
	// }
	// return result;
	// }

}
