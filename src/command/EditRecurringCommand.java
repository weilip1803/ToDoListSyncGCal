package command;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import main.POMPOM;
import utils.Item;

public class EditRecurringCommand extends Command {

	private static final String MESSAGE_DELETE_RECURRING = "A series of recurring tasks has been edited";
	
	private static final String FIELD_TYPE = "type";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_DESCRIPTION = "description";
	private static final String FIELD_PRIORITY = "priority";
	private static final String FIELD_LABEL = "label";
	
	private Long firstId;
	private Long taskId;
	boolean isUndo;
	private String field;
	private String newData;
	ArrayList<String> oldData;
	ArrayList<Item> taskList = getTaskList();

	public EditRecurringCommand(Long taskId, String field, String newData) {
		this.taskId = taskId;
		this.field = field;
		this.newData = newData;
		this.oldData = new ArrayList<String>();
		this.isUndo = false;
		
		logger.log(Level.INFO, "EditRecurringCommand initialized");
	}

	public EditRecurringCommand(Long taskId, String field, ArrayList<String> oldData) {
		this.taskId = taskId;
		this.field = field;
		this.oldData = oldData;
		this.isUndo = true;
		
		logger.log(Level.INFO, "EditRecurringCommand initialized");
	}
	
	private void updateChanges(Long taskId) {		
		
		Item currentTask = getTask(taskId);
		System.out.println(currentTask);
		
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

	private Command createCounterAction() {
		return new EditRecurringCommand(firstId, field, oldData);
	}
	
	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}
	
//	private void showCorrectTab() {
//		
//		Item task = getTask(firstId);
//		
//		if (task.getType().toLowerCase().equals(POMPOM.LABEL_EVENT.toLowerCase())) {
//			if (task.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
//				POMPOM.setCurrentTab(POMPOM.LABEL_COMPLETED_EVENT);
//			} else {
//				POMPOM.setCurrentTab(POMPOM.LABEL_EVENT);
//			}
//		} else {
//			if (task.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
//				POMPOM.setCurrentTab(POMPOM.LABEL_COMPLETED_TASK);
//			} else {
//				POMPOM.setCurrentTab(POMPOM.LABEL_TASK);
//			}
//		}
//		
//	}

	public String execute() {
		
		if (!isUndo) {
			
			this.firstId = taskId;
			while (taskId != null) {
				Item currentTask = getTask(taskId);
				Long nextId = getTask(taskId).getNextId();
				updateChanges(taskId);
				taskId = nextId;
				
				if (taskId == firstId) {
					break;
				}
			}
			
			updateUndoStack();
			POMPOM.refreshStatus();
			Item firstTask = getTask(firstId);
			showCorrectTab(firstTask);
			
			returnMsg = MESSAGE_DELETE_RECURRING;
			return returnMsg;
			
		} else {
			
			Long firstId = taskId;
			int counter = 0;
			while (taskId != null) {
				Item currentTask = getTask(taskId);
				Long nextId = getTask(taskId).getNextId();
				updateChanges(taskId, oldData.get(counter));
				taskId = nextId;
				counter++;
				
				if (taskId == firstId) {
					break;
				}
			}
			
			POMPOM.refreshStatus();
			Item firstTask = getTask(firstId);
			showCorrectTab(firstTask);

			returnMsg = MESSAGE_DELETE_RECURRING;
			return returnMsg;
			
		}
	}

}