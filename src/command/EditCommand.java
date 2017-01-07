package command;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import main.POMPOM;
import utils.Item;
/**
 * @@author wen hao
 *
 */
public class EditCommand extends Command {
	
	private static final String MESSAGE_TASK_EDITED = "%s. was successfully edited";	
	private static final String MESSAGE_TASK_ERROR = "Unable to edit task %s";
	
	private static final String FIELD_TYPE = "type";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_DESCRIPTION = "description";
	private static final String FIELD_PRIORITY = "priority";
	private static final String FIELD_STATUS = "status";
	private static final String FIELD_LABEL = "label";
	private static final String FIELD_START_DATE = "start date";
	private static final String FIELD_END_DATE = "end date";
	
	public long taskId;
	private String field;
	private String newData;
	private Date newDate;
	private Item task;
	private boolean canEdit;
	private boolean isUndo;
	
	public EditCommand(long taskId, String field, String newData) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newData = newData;
		isUndo = false;
		
		logger.log(Level.INFO, "EditCommand initialized");
	}
	
	public EditCommand(long taskId, String field, Date newDate) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newDate = newDate;
		isUndo = false;
		
		logger.log(Level.INFO, "EditCommand initialized");
	}
	
	public EditCommand(long taskId, String field, String newData, boolean isUndo) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newData = newData;
		this.isUndo = isUndo;
		
		logger.log(Level.INFO, "Counter action EditCommand initialized");
	}
	
	public EditCommand(long taskId, String field, Date newDate, boolean isUndo) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newDate = newDate;
		this.isUndo = isUndo;
		
		logger.log(Level.INFO, "Counter action EditCommand initialized");
	}
	
	private void updateChanges() {		
		
		switch (field.toLowerCase()) {
		case FIELD_TITLE :
			task.setTitle(newData);			
			break;
		case FIELD_TYPE :
			task.setType(newData);
			break;
		case FIELD_DESCRIPTION :
			task.setDescription(newData);
			break;
		case FIELD_PRIORITY :
			task.setPriority(newData);
			break;
		case FIELD_STATUS :
			task.setStatus(newData);
			break;
		case FIELD_LABEL :
			task.setLabel(newData);
			break;
		case FIELD_START_DATE :
			task.setStartDate(newDate);
			break;
		case FIELD_END_DATE :
			task.setEndDate(newDate);
			break;
		}
		
	}
	
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
	
	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}
	
	public String execute() {
		canEdit = checkExists(taskId);
		
		if (canEdit) {
			if (!isUndo) updateUndoStack();
			updateChanges();
			returnMsg = String.format(MESSAGE_TASK_EDITED, taskId);
			ArrayList<Item> taskList = getTaskList();
			POMPOM.getStorage().setTaskList(taskList);
		} else {
			returnMsg = MESSAGE_TASK_ERROR;
		}
		
		POMPOM.refreshStatus();
		showCorrectTab(task);
		
		logger.log(Level.INFO, "EditCommand has be executed");
		return returnMsg;
	}
	
}