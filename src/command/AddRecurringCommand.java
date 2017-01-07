package command;

import java.util.ArrayList;
import java.util.logging.Level;

import org.ocpsoft.prettytime.shade.org.apache.commons.lang.ObjectUtils.Null;

import main.POMPOM;
import utils.Item;

/**
 * @@author A0121528M
 */
public class AddRecurringCommand extends Command {

	/** Messaging **/
	private static final String MESSAGE_RECURRING = "Recurring tasks has been added";

	/** Command Parameters **/
	ArrayList<AddCommand> addList;
	ArrayList<Item> itemList; 
	ArrayList<Item> taskList = getTaskList();
	Long firstId = null, prevId = null, currentId, nextId = null;
	Long[] idList;
	boolean isUndo;

	
	/**
	 * Constructor for AddRecurringCommand object
	 * 
	 * @param addList
	 */
	public AddRecurringCommand(ArrayList<AddCommand> addList) {
		this.addList = addList;
		this.idList = new Long[addList.size()];
		this.isUndo = false;
		
		logger.log(Level.INFO, "AddRecurringCommand initialized");
	}
	
	
	/**
	 * Constructor for a undo AddRecurringCommand object
	 * 
	 * @param itemList
	 * @param isUndo
	 */
	public AddRecurringCommand(ArrayList<Item> itemList, boolean isUndo) {
		this.itemList = itemList;
		this.isUndo = true;
		
		logger.log(Level.INFO, "Counter AddRecurringCommand initialized");
	}
	
	
	/**
	 * Creates the reverse action for undo. For AddRecurringCommand, the reverse would be DelRecurringCommand.
	 * 
	 * @return the reverse command
	 */
	private Command createCounterAction() {
		return new DelRecurringCommand(idList);
	}
	
	
	/**
	 * Adds the reverse command into the undo stack
	 */
	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}

	
	/**
	 * Executes all the actions needed when an AddRecurringCommand is invoked
	 * 
	 * @return the appropriate feedback message 
	 */
	public String execute() {

		if (isUndo) {
			
			for (int i = 0; i < itemList.size(); i++) {
				taskList.add(itemList.get(i));
			}
			
			logger.log(Level.INFO, "AddRecurringCommand has been executed");
			returnMsg = MESSAGE_RECURRING;
			POMPOM.refreshStatus();
			showCorrectTab(itemList.get(0));
			
		} else {
			
			for (int i = 0; i < addList.size(); i++) {

				addList.get(i).execute();
				Item currentTask = taskList.get(taskList.size() - 1);
				currentId = currentTask.getId();
				
				// Sets the linking pointers
				currentTask.setRecurring(true);
				currentTask.setPrevId(prevId);
				prevId = currentId;
				idList[i] = currentId;

				// Saves the firstId
				if (i == 0) {
					firstId = currentId;
				}
				
				if (i == addList.size() - 1) {
					nextId = firstId;
				} else {
					nextId = currentId + 1;
				}
				
				currentTask.setNextId(nextId);

			}
			
			updateUndoStack();
			POMPOM.refreshStatus();
			//showCorrectTab(getTask(firstId));
			logger.log(Level.INFO, "AddRecurringCommand has been executed");
			returnMsg = MESSAGE_RECURRING;
		}
		
		return returnMsg;
	}

}