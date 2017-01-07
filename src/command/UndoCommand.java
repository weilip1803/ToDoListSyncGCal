package command;

import java.util.Stack;
import java.util.logging.Level;

/**
 * @@author wen hao
 *
 */
import main.POMPOM;

public class UndoCommand extends Command {

	private static final String MESSAGE_UNDO = "Previous action has been successfully undone";
	private static final String MESSAGE_ERROR = "There is nothing to undo";

	public String execute() {
		
		Stack<Command> undoStack = POMPOM.getUndoStack();
		
		// checks if stack is empty as popping an empty stack will cause exceptions
		if (undoStack.isEmpty()) {
			returnMsg = MESSAGE_ERROR;
			return returnMsg;
		} else {
			Command undo = undoStack.pop();
			undo.execute();
			logger.log(Level.INFO, "UndoCommand has be executed");
			returnMsg = MESSAGE_UNDO;
			return returnMsg;
		}
	}
	
}
