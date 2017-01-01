package command;

import java.util.ArrayList;
import java.util.logging.Level;

import main.POMPOM;
import utils.Item;

/**
 * @@author A0121528M
 */
public class DelRecurringCommand extends Command {

	/** Messaging **/
	public static final String MESSAGE_DELETE_RECURRING = "A series of recurring tasks has been deleted";
	public static final String MESSAGE_DELETE_RECUR_ERROR = "%s is not a reuccring task!";
	public static final String MESSAGE_INVALID = "%s is not a valid ID!";
	
	/** Command Parameters **/
	private Long taskId;
	private Long[] idList;
	boolean isUndo;
	ArrayList<Item> addList;
	ArrayList<Item> taskList = getTaskList();

	/**
	 * Constructor for DelRecurringCommand object
	 * 
	 * @param taskId
	 */
	public DelRecurringCommand(Long taskId) {
		this.taskId = taskId;
		this.addList = new ArrayList<Item>(); 
		this.isUndo = false;

		logger.log(Level.INFO, "DelRecurringCommand initialized");
	}
	
	/**
	 * Constructor for an undo DelRecurringCommand object
	 * 
	 * @param idList
	 */
	public DelRecurringCommand(Long[] idList) {
		this.idList = idList;
		this.isUndo = true;

		logger.log(Level.INFO, "Counter DelRecurringCommand initialized");
	}

	/**
	 * Method that do the actual removing of task from storage
	 */
	private void removeTask(Long taskId) {

		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getId().equals(taskId)) {
				taskList.remove(i);
			}
		}
		POMPOM.getStorage().setTaskList(taskList);

	}

	/**
	 * Creates the reverse action for undo. 
	 * For DelRecurringCommand, the reverse would be AddRecurringCommand.
	 * 
	 * @return the reverse command
	 */
	private Command createCounterAction() {
		return new AddRecurringCommand(addList, true);
	}
	
	/**
	 * Adds the reverse command into the undo stack
	 */
	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}

	/**
	 * Executes all the actions needed when a DelRecurringCommand is invoked
	 * 
	 * @return the appropriate feedback message 
	 */
	public String execute() {

		if (!isUndo) {
			
			Long firstId = taskId;
			Item firstTask = getTask(firstId); 
			
			if(firstTask == null){
				return String.format(MESSAGE_INVALID, firstId);
			} 			
			
			if(!firstTask.isRecurring()|| firstTask.getId() == null){
				returnMsg = String.format(MESSAGE_DELETE_RECUR_ERROR, firstId);
				return returnMsg;
			}
			
			while (true) {
				
				Item currentTask = getTask(taskId);
				
				addList.add(currentTask);
					
				Long nextId = getTask(taskId).getNextId();
				removeTask(taskId);
				taskId = nextId;
				
				if (taskId.equals(firstId)) {
					break;
				}
				
			} 
			
			updateUndoStack();
			POMPOM.refreshStatus();
			showCorrectTab(firstTask);

			returnMsg = MESSAGE_DELETE_RECURRING;
			return returnMsg;
			
		} else {
			
			Item firstTask = getTask(idList[0]);
			for (int i = 0; i < idList.length; i++) {
				Long currentId = idList[i];
				removeTask(currentId);
			}
			
			POMPOM.refreshStatus();
			showCorrectTab(firstTask);
			
			returnMsg = MESSAGE_DELETE_RECURRING;
			return returnMsg;
			
		}
	}

}