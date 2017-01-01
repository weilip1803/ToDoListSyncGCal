package command;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.POMPOM;
import utils.Item;

/**
 * @@author A0121528M
 */
public class DelCommand extends Command {

	/** Messaging **/
	public static final String MESSAGE_TASK_DELETED = "%1s has been deleted from %2s";
	public static final String MESSAGE_ID_INVALID = "%s is not a valid ID!";

	/** Command Parameters **/
	private Long taskId;
	private boolean isUndo;
	private boolean canDelete;
	private Item toDelete;
	ArrayList<Item> taskList = getTaskList();

	/**
	 * Constructor for a normal delete command
	 * 
	 * @param taskId
	 */
	public DelCommand(Long taskId) {
		this.taskId = taskId;
		this.isUndo = false;
		this.toDelete = getTask(taskId);

		logger.log(Level.INFO, "DelCommand with id initialized");
	}

	/**
	 * Constructor for an undo delete command
	 * 
	 * @param taskId
	 * @param isUndo
	 */
	public DelCommand(Long taskId, boolean isUndo) {
		this.taskId = taskId;
		this.isUndo = isUndo;

		logger.log(Level.INFO, "Counter action DelCommand initialized");
	}

	/**
	 * Method that do the actual removing of task from storage
	 */
	private void removeTask() {

		ArrayList<Item> taskList = getTaskList();
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getId().equals(taskId)) {
				taskList.remove(i);
			}
		}
		POMPOM.getStorage().setTaskList(taskList);
	}

	/**
	 * Creates the reverse action for undo. For DelCommand, the reverse would be
	 * AddCommand.
	 * 
	 * @return the reverse command
	 */
	private Command createCounterAction() {
		AddCommand counterAction = new AddCommand(toDelete.getId(), toDelete.getType(), toDelete.getTitle(),
				toDelete.getDescription(), toDelete.getPriority(), toDelete.getStatus(), toDelete.getLabel(),
				toDelete.getStartDate(), toDelete.getEndDate(), toDelete.isRecurring(), toDelete.getPrevId(),
				toDelete.getNextId());
		return counterAction;
	}

	/**
	 * Adds the reverse command into the undo stack
	 */
	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}

	/**
	 * Sets the pointer of the recurring tasks. Like a linked list, when the
	 * middle element is deleted, the pointers of the previous node should link
	 * to the next node.
	 */
	private void setProperPointers() {

		Item currentTask = getTask(toDelete.getId());

		Item prevTask = getTask(currentTask.getPrevId()); 
		prevTask.setNextId(currentTask.getNextId());

		Item nextTask = getTask(currentTask.getNextId());
		nextTask.setPrevId(currentTask.getPrevId());

	}

	/**
	 * Sets the proper return message to return
	 */
	private void setProperReturnMessage() {

		if (canDelete) {
			if (toDelete.getType().toLowerCase().equals(POMPOM.LABEL_EVENT.toLowerCase())) {
				if (toDelete.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
					returnMsg = String.format(MESSAGE_TASK_DELETED, taskId, POMPOM.LABEL_COMPLETED_EVENT);
				} else {
					returnMsg = String.format(MESSAGE_TASK_DELETED, taskId, POMPOM.LABEL_EVENT);
				}
			} else {
				if (toDelete.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
					returnMsg = String.format(MESSAGE_TASK_DELETED, taskId, POMPOM.LABEL_COMPLETED_TASK);
				} else {
					returnMsg = String.format(MESSAGE_TASK_DELETED, taskId, POMPOM.LABEL_TASK);
				}
			}
		} else {
			returnMsg = String.format(MESSAGE_ID_INVALID, taskId);
		}
	}

	/**
	 * Executes all the actions needed when a delete command is invoked
	 * 
	 * @return the appropriate feedback message
	 */
	public String execute() {

		canDelete = checkExists(taskId);

		toDelete = getTask(taskId);

		if (canDelete) {

			if (toDelete.isRecurring()) {
				setProperPointers();
			}

			if (!isUndo) {
				updateUndoStack();
			}

			removeTask();
			logger.log(Level.INFO, "DelCommand by Id has be executed");

			POMPOM.refreshStatus();
			showCorrectTab(toDelete);
			setProperReturnMessage();
			return returnMsg;

		} else {
			setProperReturnMessage();
			return returnMsg;
		}
	}
}