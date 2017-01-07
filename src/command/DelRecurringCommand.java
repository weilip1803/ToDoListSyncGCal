package command;

import java.util.ArrayList;
import java.util.logging.Level;

import org.omg.CORBA.Current;

import main.POMPOM;
import utils.Item;

public class DelRecurringCommand extends Command {

	private static final String MESSAGE_DELETE_RECURRING = "A series of recurring tasks has been deleted";
	private Long taskId;
	private Long[] idList;
	boolean isUndo;
	ArrayList<Item> addList;
	ArrayList<Item> taskList = getTaskList();

	public DelRecurringCommand(Long taskId) {
		this.taskId = taskId;
		this.addList = new ArrayList<Item>(); 
		this.isUndo = false;

		logger.log(Level.INFO, "DelRecurringCommand initialized");
	}

	public DelRecurringCommand(Long[] idList) {
		this.idList = idList;
		this.isUndo = true;

		logger.log(Level.INFO, "Counter DelRecurringCommand initialized");
	}

	private void removeTask(Long taskId) {

		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getId() == taskId) {
				taskList.remove(i);
			}
		}
		POMPOM.getStorage().setTaskList(taskList);

	}

	private Command createCounterAction() {
		return new AddRecurringCommand(addList, true);
	}
	
	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}

	public String execute() {

		if (!isUndo) {
			
			Long firstId = taskId;
			Item firstTask = getTask(firstId);
			while (taskId != null) {
				Item currentTask = getTask(taskId);
				addList.add(currentTask);
				Long nextId = getTask(taskId).getNextId();
				removeTask(taskId);
				taskId = nextId;
				
				if (taskId == firstId) {
					break;
				}
			}
			
			updateUndoStack();
			POMPOM.refreshStatus();
			showCorrectTab(firstTask);

			returnMsg = MESSAGE_DELETE_RECURRING;
			return returnMsg;
			
		} else {
			
			//Item toDelete = getTask(taskId);
			for (int i = 0; i < idList.length; i++) {
				long currentId = idList[i];
				removeTask(currentId);
			}
			
			
			POMPOM.refreshStatus();
			//showCorrectTab(toDelete);
			
			returnMsg = MESSAGE_DELETE_RECURRING;
			return returnMsg;
			
		}
	}

}
