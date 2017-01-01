package command;

import java.util.Stack;
import java.util.logging.Level;
import main.POMPOM;

/**
 * @@author A0121528M
 */
public class UndoCommand extends Command {

	/** Messaging **/
	public static final String MESSAGE_UNDO = "Previous action has been successfully undone";
	public static final String MESSAGE_ERROR = "There is nothing to undo";

	/**
	 * Executes all the actions needed when an UndoCommand is invoked
	 * 
	 * @return the appropriate feedback message
	 */
	public String execute() {

		Stack<Command> undoStack = POMPOM.getUndoStack();

		// checks if stack is empty as popping an empty stack will cause
		// exceptions
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
