package parser;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LoggingPermission;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

import command.Command;
import command.InvalidCommand;
import command.PathCommand;
import main.POMPOM;

/**
 * @@author Josh
 *
 */
public class Parser {

	/** List of Command types */
	private static final String CMD_ADD = "add";
	private static final String CMD_DELETE = "delete";
	private static final String CMD_DONE = "done";
	private static final String CMD_EDIT = "edit";
	private static final String CMD_EXIT = "exit";
	private static final String CMD_SEARCH = "search";
	private static final String CMD_SHOW = "show";
	private static final String CMD_UNDO = "undo";
	private static final String CMD_PATH = "setpath";
	private static final String CMD_EVENT = "event";
	private static final String CMD_HELP_1 = "help";
	private static final String CMD_HELP_2 = "?";
	private static final String CMD_DELETE_RECUR_1 = "delete recur";
	private static final String CMD_EDIT_RECUR_1 = "edit recur";
	private static final String CMD_DELETE_RECUR_2 = "delete r";
	private static final String CMD_EDIT_RECUR_2 = "edit r";
	private static final String CMD_VIEW = "view";
	
	/** List of Invalid command types */
	private static final String INVALID_CMD_MESSAGE = "%s: Command does not exist";



//	private static final int COMMAND_ARRAY_SIZE = 2;
//	private static final int COMMAND_TYPE_INDEX = 0;
//	private static final int COMMAND_ARGUMENT_INDEX = 1;

	private static Parser parserInstance;

	private static Logger logger = Logger.getLogger("Parser");

	public static Parser getInstance() {
		if (parserInstance == null)
			parserInstance = new Parser();

		return parserInstance;
	}

	private Parser() {
		
	}

	/**
	 * This operation takes in the command specified by the user, executes it
	 * and returns a message about the execution information to the user.
	 * 
	 * @param userCommand
	 *            is the command the user has given to the program
	 * @return the message containing information about the execution of the
	 *         command.
	 */
	public Command parse(String userCommand) {

		
		String commandType = getStringCommand(userCommand);
		String commandArgument = getStringArgs(userCommand);
		
		System.out.println(commandType);
		switch (commandType) {
		case CMD_ADD :
			AddParser addTaskArgumentParser = new AddParser(commandArgument,
					POMPOM.LABEL_TASK);
			return addTaskArgumentParser.parse();
		case CMD_EVENT :
			AddParser addEventArgumentParser = new AddParser(commandArgument,
					POMPOM.LABEL_EVENT);
			return addEventArgumentParser.parse();
		case CMD_DELETE :
			DeleteParser deleteArgumentParser = new DeleteParser(
					commandArgument);
			return deleteArgumentParser.parse();
		case CMD_EDIT :
			EditParser EditArgumentParser = new EditParser(commandArgument);
			return EditArgumentParser.parse();
		case CMD_SEARCH :
			SearchParser searchParser = new SearchParser(commandArgument);
			return searchParser.parse();
		case CMD_SHOW :
			// return new ShowParser(commandArgument);
			// Hard coded
		case CMD_EXIT :
			ExitParser exitParser = new ExitParser();
			// return exitParser.executeCommand();
			System.exit(0);
		case CMD_UNDO :
			UndoParser undoParser = new UndoParser();
			return undoParser.parse();
		case CMD_HELP_1 :
		case CMD_HELP_2 :
			HelpParser helpParser = new HelpParser();
			return helpParser.parse();
		case CMD_PATH :
			return new PathCommand(commandArgument);
		case CMD_DELETE_RECUR_1 :
		case CMD_DELETE_RECUR_2 :
			return new command.DelRecurringCommand(
					Long.parseLong(commandArgument));
		case CMD_EDIT_RECUR_1 :
		case CMD_EDIT_RECUR_2 :
			EditRecurringParser EditRecurringArgumentParser = new EditRecurringParser(
					commandArgument);
			return EditRecurringArgumentParser.parse();
		case CMD_VIEW :
			ViewParser viewParser = new ViewParser(commandArgument);
			return viewParser.parse();
		case CMD_DONE :
			DoneParser DoneArgumentParser = new DoneParser(commandArgument);
			return DoneArgumentParser.executeCommand();
		default : 
			String returnMsg = String.format(INVALID_CMD_MESSAGE, commandType);
			InvalidCommand invalidCommand = new InvalidCommand(returnMsg);
			return invalidCommand;
		}
//		InvalidParser InvalidArgumentParser = new InvalidParser(userCommand);
//		return InvalidArgumentParser.executeCommand();

	}
	
	private static String getStringCommand(String userInput) {
		String[] toSplit = userInput.split(" ", 2);
		return toSplit[0].toLowerCase().trim();
	}
	
	private static String getStringArgs(String userInput) {
		String[] toSplit = userInput.split(" ", 2);
		if (toSplit.length > 1) {
			return toSplit[1].trim();
		} else {
			return null;
		}
	}
	


}
