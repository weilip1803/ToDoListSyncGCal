package command;

import java.util.ArrayList;
import java.util.logging.Logger;

import main.POMPOM;
import utils.Item;

/**
 * @@author A0121528M
 */
public abstract class Command {

	/** Messaging **/
	protected String returnMsg = "";

	public static Logger logger = Logger.getLogger("Command");

	public Command() {

	} 

	/**
	 * Method to get the item with the specified taskId. 
	 * Method is shared among all commands.
	 * 
	 * @param taskId
	 * @return the item with the specified id
	 */
	protected Item getTask(Long taskId) {
		ArrayList<Item> taskList = getTaskList();
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getId().equals(taskId)) {
				return taskList.get(i);
			}
		}
		return null;
	}

	/**
	 * Method to get the list of all stored items. 
	 * Method is shared among all commands.
	 * 
	 * @return the array list of all stored items
	 */
	protected ArrayList<Item> getTaskList() {
		return POMPOM.getStorage().getTaskList();
	}

	/**
	 * Method to check the existence an item with the specified id. 
	 * Method is shared among all commands.
	 * 
	 * @param taskId
	 * @return boolean of whether id exists
	 */
	protected boolean checkExists(Long taskId) {

		Item toDelete = null;
		try {
			toDelete = getTask(taskId);
		} catch (IndexOutOfBoundsException e) {

		}

		if (toDelete == null) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Method to display the correct tab after an action. 
	 * The tab selected for viewing is where the specified item reside in. 
	 * Method is shared among all commands.
	 * 
	 * @param item
	 */
	protected void showCorrectTab(Item item) {

		if (item.getType().toLowerCase().equals(POMPOM.LABEL_EVENT.toLowerCase())) {
			if (item.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
				POMPOM.setCurrentTab(POMPOM.LABEL_COMPLETED_EVENT);
			} else {
				POMPOM.setCurrentTab(POMPOM.LABEL_EVENT);
			}
		} else {
			if (item.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
				POMPOM.setCurrentTab(POMPOM.LABEL_COMPLETED_TASK);
			} else {
				POMPOM.setCurrentTab(POMPOM.LABEL_TASK);
			}
		}

	}

	/**
	 * Executes all the actions needed when the command is invoked.
	 * 
	 * @return the appropriate feedback message 
	 */
	public abstract String execute();

}
