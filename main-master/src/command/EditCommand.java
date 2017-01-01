package command;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import main.POMPOM;
import utils.Item;

/**
 * @@author A0121528M
 */
public class EditCommand extends Command {

	/** Messaging **/
	public static final String MESSAGE_TASK_EDITED = "%s was successfully edited";
	public static final String MESSAGE_TASK_ERROR = "%s is not a valid ID!";
	public static final String MESSAGE_DATE_CHANGE_ERROR = "Start date must be earlier then end date";

	/** Input Field Types **/
	private static final String FIELD_TYPE = "type";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_DESCRIPTION = "description";
	private static final String FIELD_PRIORITY = "priority";
	private static final String FIELD_STATUS = "status";
	private static final String FIELD_LABEL = "label";
	private static final String FIELD_START_DATE = "start date";
	private static final String FIELD_END_DATE = "end date";

	/** Command Parameters **/
	public Long taskId;
	private String field;
	private String newData;
	private Date newDate;
	private Item task;
	private boolean canEdit;
	private boolean isUndo;

	/**
	 * Constructor for a normal edit command with String data
	 * 
	 * @param taskId
	 * @param field
	 * @param newData
	 */
	public EditCommand(Long taskId, String field, String newData) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newData = newData;
		isUndo = false;

		logger.log(Level.INFO, "EditCommand initialized");
	}

	/**
	 * Constructor for a normal edit command with Date data
	 * 
	 * @param taskId
	 * @param field
	 * @param newDate
	 */
	public EditCommand(Long taskId, String field, Date newDate) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newDate = newDate;
		isUndo = false;

		logger.log(Level.INFO, "EditCommand initialized");
	}

	/**
	 * Constructor for an undo edit command with String data
	 * 
	 * @param taskId
	 * @param field
	 * @param newData
	 * @param isUndo
	 */
	public EditCommand(Long taskId, String field, String newData, boolean isUndo) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newData = newData;
		this.isUndo = isUndo;

		logger.log(Level.INFO, "Counter action EditCommand initialized");
	}

	/**
	 * Constructor for an undo edit command with Date data
	 * 
	 * @param taskId
	 * @param field
	 * @param newDate
	 * @param isUndo
	 */
	public EditCommand(Long taskId, String field, Date newDate, boolean isUndo) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newDate = newDate;
		this.isUndo = isUndo;

		logger.log(Level.INFO, "Counter action EditCommand initialized");
	}

	/**
	 * Method that carries out the actual editing action
	 */
	private void updateChanges() {

		switch (field.toLowerCase()) {
		case FIELD_TITLE:
			task.setTitle(newData);
			break;
		case FIELD_TYPE:
			task.setType(newData);
			break;
		case FIELD_DESCRIPTION:
			task.setDescription(newData);
			break;
		case FIELD_PRIORITY:
			task.setPriority(newData);
			break;
		case FIELD_STATUS:
			task.setStatus(newData);
			break;
		case FIELD_LABEL:
			task.setLabel(newData);
			break;
		case FIELD_START_DATE:
			task.setStartDate(newDate);
			break;
		case FIELD_END_DATE:
			task.setEndDate(newDate);
			break;
		}

	}

	/**
	 * Creates the reverse action for undo. For EditCommand, the reverse would
	 * be another EditCommand with the old data.
	 * 
	 * @return the reverse command
	 */
	private Command createCounterAction() {
		EditCommand counterAction;

		switch (field.toLowerCase()) {
		case FIELD_TITLE:
			counterAction = new EditCommand(taskId, field, task.getTitle(), true);
			break;
		case FIELD_DESCRIPTION:
			counterAction = new EditCommand(taskId, field, task.getDescription(), true);
			break;
		case FIELD_PRIORITY:
			counterAction = new EditCommand(taskId, field, task.getPriority(), true);
			break;
		case FIELD_STATUS:
			counterAction = new EditCommand(taskId, field, task.getStatus(), true);
			break;
		case FIELD_LABEL:
			counterAction = new EditCommand(taskId, field, task.getLabel(), true);
			break;
		case FIELD_START_DATE:
			counterAction = new EditCommand(taskId, field, task.getStartDate(), true);
			break;
		case FIELD_END_DATE:
			counterAction = new EditCommand(taskId, field, task.getEndDate(), true);
			break;
		default:
			counterAction = null;
			break;
		}

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
	 * Executes all the actions needed when an edit command is invoked
	 * 
	 * @return the appropriate feedback message
	 */
	public String execute() {

		canEdit = checkExists(taskId);

		if (canEdit) {
			if (!isUndo) {
				updateUndoStack();
			}
			updateChanges();

			// Ensures that end date is after start date
			if (task.getStartDate() != null && task.getEndDate() != null) {
				if (task.getEndDate().compareTo(task.getStartDate()) < 0) {
					POMPOM.getUndoStack().pop().execute();
					returnMsg = MESSAGE_DATE_CHANGE_ERROR;
					return returnMsg;
				}
			}

			returnMsg = String.format(MESSAGE_TASK_EDITED, taskId);
			ArrayList<Item> taskList = getTaskList();
			POMPOM.getStorage().setTaskList(taskList);

		} else {
			
			returnMsg = String.format(MESSAGE_TASK_ERROR, taskId);
			return returnMsg;
		}

		POMPOM.refreshStatus();
		showCorrectTab(task);

		logger.log(Level.INFO, "EditCommand has be executed");
		return returnMsg;
	}

}