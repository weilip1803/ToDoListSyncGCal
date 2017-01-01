package command;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import main.POMPOM;
import utils.Item;

/**
 * @@author A0121528M
 */
public class EditRecurringCommand extends Command {

	/** Messaging **/
	public static final String MESSAGE_EDIT_RECURRING = "A series of recurring tasks has been edited";
	public static final String MESSAGE_EDIT_RECUR_ERROR = "%s is not a reuccring task!";

	/** Input Field Types **/
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_DESCRIPTION = "description";
	private static final String FIELD_PRIORITY = "priority";
	private static final String FIELD_LABEL = "label";

	/** Command Parameters **/
	private Long firstId;
	private Long taskId;
	boolean isUndo;
	private String field;
	private String newData;
	ArrayList<String> oldData;
	ArrayList<Item> taskList = getTaskList();

	/**
	 * Constructor for EditRecurringCommand object
	 * 
	 * @param taskId
	 * @param field
	 * @param newData
	 */
	public EditRecurringCommand(Long taskId, String field, String newData) {
		this.taskId = taskId;
		this.field = field;
		this.newData = newData;
		this.oldData = new ArrayList<String>();
		this.isUndo = false;

		logger.log(Level.INFO, "EditRecurringCommand initialized");
	}

	/**
	 * Constructor for an undo EditRecurringCommand object
	 * 
	 * @param taskId
	 * @param field
	 * @param oldData
	 */
	public EditRecurringCommand(Long taskId, String field,
			ArrayList<String> oldData) {
		this.taskId = taskId;
		this.field = field;
		this.oldData = oldData;
		this.isUndo = true;

		logger.log(Level.INFO, " Counter EditRecurringCommand initialized");
	}

	/**
	 * Method that carries out the actual editing action. This is for the normal
	 * Edit Recurring as the old data needs to be saved.
	 * 
	 * @param taskId
	 */
	private void updateChanges(Long taskId) {

		Item currentTask = getTask(taskId);

		switch (field.toLowerCase()) {
		case FIELD_TITLE:
			oldData.add(currentTask.getTitle());
			currentTask.setTitle(newData);
			break;
		case FIELD_DESCRIPTION:
			oldData.add(currentTask.getDescription());
			currentTask.setDescription(newData);
			break;
		case FIELD_PRIORITY:
			oldData.add(currentTask.getPriority());
			currentTask.setPriority(newData);
			break;
		case FIELD_LABEL:
			oldData.add(currentTask.getLabel());
			currentTask.setLabel(newData);
			break;
		}

	}

	/**
	 * Method that carries out the actual editing action. This is for the undo
	 * Edit Recurring as the old data needs to be specified.
	 * 
	 * @param taskId
	 * @param oldData
	 */
	private void updateChanges(Long taskId, String oldData) {

		Item currentTask = getTask(taskId);

		switch (field.toLowerCase()) {
		case FIELD_TITLE:
			currentTask.setTitle(oldData);
			break;
		case FIELD_DESCRIPTION:
			currentTask.setDescription(oldData);
			break;
		case FIELD_PRIORITY:
			currentTask.setPriority(oldData);
			break;
		case FIELD_LABEL:
			currentTask.setLabel(oldData);
			break;
		}

	}

	/**
	 * Creates the reverse action for undo. For EditRecurringCommand, the
	 * reverse would be another EditRecurringCommand with the old data.
	 * 
	 * @return the reverse command
	 */
	private Command createCounterAction() {
		return new EditRecurringCommand(firstId, field, oldData);
	}

	/**
	 * Adds the reverse command into the undo stack
	 */
	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}

	/**
	 * Executes all the actions needed when an EditRecurringCommand is invoked
	 * 
	 * @return the appropriate feedback message
	 */
	public String execute() {

		this.firstId = taskId;
		Item firstTask = getTask(firstId);

		if (firstTask.isRecurring()) {
			if (!isUndo) {

				while (taskId != null) {
					Item currentTask = getTask(taskId);

					Long nextId = currentTask.getNextId();
					updateChanges(taskId);
					taskId = nextId;

					if (taskId.equals(firstId)) {
						break;
					}
				}

				updateUndoStack();
				POMPOM.refreshStatus();
				showCorrectTab(firstTask);

				returnMsg = MESSAGE_EDIT_RECURRING;
				return returnMsg;

			} else {

				Long firstId = taskId;
				int counter = 0;

				while (taskId != null) {
					Long nextId = getTask(taskId).getNextId();
					updateChanges(taskId, oldData.get(counter));
					taskId = nextId;
					counter++;

					if (taskId.equals(firstId)) {
						break;
					}
				}

				POMPOM.refreshStatus();
				showCorrectTab(firstTask);

				returnMsg = MESSAGE_EDIT_RECURRING;
				return returnMsg;

			}
		} else {

			returnMsg = String.format(MESSAGE_EDIT_RECUR_ERROR, firstId);
			return returnMsg;
		}

	}

}