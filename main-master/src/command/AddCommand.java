package command;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import main.POMPOM;
import utils.Item;

/**
 * @@author A0121528M
 */
public class AddCommand extends Command {

	/** Messaging **/
	public static final String MESSAGE_TASK_ADDED = "%s added";

	/** Command parameters **/
	private Item task;
	private boolean isUndo;
	
	/**
	 * Constructor for a normal add command
	 * 
	 * @param type
	 * @param title
	 * @param description
	 * @param priority
	 * @param status
	 * @param label
	 * @param startDate
	 * @param endDate
	 */
	public AddCommand(String type, String title, String description, String priority, String status, String label,
			Date startDate, Date endDate) {

		task = new Item();
		isUndo = false;
		assertNotNull(title);

		Long id = POMPOM.getStorage().getIdCounter();
		task.setId(id);
		task.setType(type);
		task.setTitle(title);
		task.setDescription(description);
		task.setPriority(priority);
		task.setStatus(status);
		task.setLabel(label);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setRecurring(false);

		logger.log(Level.INFO, "AddCommand initialized");
	}

	/**
	 * Constructor for a recurring add command
	 * 
	 * @param type
	 * @param title
	 * @param description
	 * @param priority
	 * @param status
	 * @param label
	 * @param startDate
	 * @param endDate
	 * @param isRecurring
	 */
	public AddCommand(String type, String title, String description, String priority, String status, String label,
			Date startDate, Date endDate, boolean isRecurring) {

		task = new Item();
		isUndo = false;
		assertNotNull(title);

		Long id = POMPOM.getStorage().getIdCounter();
		task.setId(id);
		task.setType(type);
		task.setTitle(title);
		task.setDescription(description);
		task.setPriority(priority);
		task.setStatus(status);
		task.setLabel(label);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setRecurring(true);

		logger.log(Level.INFO, "Recurring AddCommand initialized");
	}

	/**
	 * Constructor for an undo add command
	 * 
	 * @param id
	 * @param type
	 * @param title
	 * @param description
	 * @param priority
	 * @param status
	 * @param label
	 * @param startDate
	 * @param endDate
	 * @param isRecurring
	 * @param prevId
	 * @param nextId
	 */
	public AddCommand(Long id, String type, String title, String description, String priority, String status,
			String label, Date startDate, Date endDate, boolean isRecurring, Long prevId, Long nextId) {

		task = new Item();
		isUndo = true;
		assertNotNull(title);

		task.setId(id);
		task.setType(type);
		task.setTitle(title);
		task.setDescription(description);
		task.setPriority(priority);
		task.setStatus(status);
		task.setLabel(label);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setRecurring(isRecurring);
		task.setPrevId(prevId);
		task.setNextId(nextId);

		logger.log(Level.INFO, "Counter action AddCommand initialized");
	}

	/**
	 * Carries out the actual action of add task into taskList
	 */
	private void storeTask() {
		POMPOM.getStorage().getTaskList().add(task);
	}

	/**
	 * Creates the reverse action for undo. For AddCommand, the reverse would be
	 * DelCommand.
	 * 
	 * @return the reverse command
	 */
	private Command createCounterAction() {
		DelCommand counterAction = new DelCommand(task.getId(), true);
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
	 * 
	 * Now after deleting, when the task is added back by undo, the reverse
	 * linking process should take place
	 */
	private void setProperPointers() {

		Item currentTask = task;

		Item prevTask = getTask(currentTask.getPrevId());
		prevTask.setNextId(currentTask.getId());

		Item nextTask = getTask(currentTask.getNextId());
		nextTask.setPrevId(currentTask.getId());

	}

	/**
	 * Sets the proper return message to return
	 */
	private void setProperReturnMsg() {

		if (task.getType().equals(POMPOM.LABEL_EVENT)) {
			returnMsg = String.format(MESSAGE_TASK_ADDED, POMPOM.LABEL_EVENT);
		} else {
			returnMsg = String.format(MESSAGE_TASK_ADDED, POMPOM.LABEL_TASK);
		}

	}

	/**
	 * Executes all the actions needed when a add command is invoked
	 * 
	 * @return the appropriate feedback message
	 */
	public String execute() {

		if (!isUndo && !task.isRecurring()) {
			updateUndoStack();
		}

		storeTask();

		if (isUndo && task.isRecurring()) {
			setProperPointers();
		}

		setProperReturnMsg();
		POMPOM.refreshStatus();
		showCorrectTab(task);

		logger.log(Level.INFO, "AddCommand has be executed");
		return returnMsg;
	}

}