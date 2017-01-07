package command;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.POMPOM;
import utils.Item;

/**
 * @@author wen hao
 *
 */
public class DelCommand extends Command {

	private static final String MESSAGE_TASK_DELETED_ID = "%1s has been deleted from %2s";
	private static final String MESSAGE_TASK_ERROR = "Unable to delete";

	private long taskId;
	private String taskTitle;
	private boolean isById;
	private boolean isUndo;
	private boolean canDelete;
	private Item toDelete;
	ArrayList<Item> taskList = getTaskList();

	public DelCommand(long taskId) {
		this.taskId = taskId;
		this.isById = true;
		this.isUndo = false;
		this.toDelete = getTask(taskId);

		logger.log(Level.INFO, "DelCommand with id initialized");
	}

	public DelCommand(String taskTitle) {
		this.taskTitle = taskTitle;
		this.isById = false;
		this.isUndo = false;
		this.toDelete = getTask(taskId);

		logger.log(Level.INFO, "DelCommand with title initialized");
	}

	public DelCommand(long taskId, boolean isUndo) {
		this.taskId = taskId;
		this.isUndo = isUndo;
		this.isById = true;

		logger.log(Level.INFO, "Counter action DelCommand initialized");
	}

	private void removeTask() {
		if (isById) {

			ArrayList<Item> taskList = getTaskList();
			for (int i = 0; i < taskList.size(); i++) {
				if (taskList.get(i).getId() == taskId) {
					taskList.remove(i);
				}
			}
			POMPOM.getStorage().setTaskList(taskList);

		} else {

			ArrayList<Item> taskList = getTaskList();
			for (int i = 0; i < taskList.size(); i++) {
				if (taskList.get(i).getTitle().toLowerCase().equals(taskTitle.toLowerCase())) {
					taskList.remove(i);
				}
			}
			POMPOM.getStorage().setTaskList(taskList);

		}
	}

	private Command createCounterAction() {
		AddCommand counterAction = new AddCommand(toDelete.getId(), toDelete.getType(), toDelete.getTitle(),
				toDelete.getDescription(), toDelete.getPriority(), toDelete.getStatus(), toDelete.getLabel(),
				toDelete.getStartDate(), toDelete.getEndDate(), toDelete.isRecurring(), toDelete.getPrevId(),
				toDelete.getNextId());
		return counterAction;
	}

	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}

	private void setProperPointers() {
		Item currentTask = getTask(toDelete.getId());

		if (!(currentTask.getPrevId() == null)) {
			Item prevTask = getTask(currentTask.getPrevId());
			prevTask.setNextId(currentTask.getNextId());
		}

		if (!(currentTask.getNextId() < currentTask.getId())) {
			Item nextTask = getTask(currentTask.getNextId());
			nextTask.setPrevId(currentTask.getPrevId());
		}

	}

	private void setProperReturnMessage() {

		if (canDelete) {

			if (toDelete.getType().toLowerCase().equals(POMPOM.LABEL_EVENT.toLowerCase())) {
				if (toDelete.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
					returnMsg = String.format(MESSAGE_TASK_DELETED_ID, taskId, POMPOM.LABEL_COMPLETED_EVENT);
				} else {
					returnMsg = String.format(MESSAGE_TASK_DELETED_ID, taskId, POMPOM.LABEL_EVENT);
				}
			} else {
				if (toDelete.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
					returnMsg = String.format(MESSAGE_TASK_DELETED_ID, taskId, POMPOM.LABEL_COMPLETED_TASK);
				} else {
					returnMsg = String.format(MESSAGE_TASK_DELETED_ID, taskId, POMPOM.LABEL_TASK);
				}
			}

		} else {
			returnMsg = MESSAGE_TASK_ERROR;
		}

	}

	public String execute() {

		canDelete = checkExists(taskId);

		toDelete = getTask(taskId);

		if (toDelete.isRecurring()) {
			setProperPointers();
		}

		if (canDelete) {

			if (!isUndo) {
				updateUndoStack();
			}

			removeTask();
			logger.log(Level.INFO, "DelCommand by Id has be executed");

		}

		POMPOM.refreshStatus();
		showCorrectTab(toDelete);
		setProperReturnMessage();
		return returnMsg;
	}

}