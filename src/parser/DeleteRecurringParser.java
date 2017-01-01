package parser;

import command.Command;
import command.InvalidCommand;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;

/**
 * @author A0121760R
 *
 */
public class DeleteRecurringParser extends ArgsParser{
	
	private static final String MESSAGE_ID_NOT_NUMBER_ERROR = "Please enter the task ID!";
	private static final String MESSAGE_INVALID_ID_ERROR = "Please key in an id";
	Command invalidCommand=null; 
	Long recurringItemId;
	private static final String LOG_CREATE_DELETE_RECURRING_PARSER = "DeleteRecuringParser Created for \"%s\"";

	public DeleteRecurringParser(String commandArgument){
		super(commandArgument);
		assertNotNull(commandArgumentsString);
		try{
			recurringItemId  = Long.parseLong(commandArgument);
		} catch (Exception e){
			invalidCommand = new InvalidCommand(MESSAGE_ID_NOT_NUMBER_ERROR);
		}
		logger.log(Level.INFO, String.format(LOG_CREATE_DELETE_RECURRING_PARSER, commandArgumentsString));
	}
	
	public Command parse(){
		if (isInvalidID()){
			invalidCommand = new InvalidCommand(MESSAGE_INVALID_ID_ERROR);
		} 
		if (hasInvalidCommand()){
			return invalidCommand;
		} else {
			return new command.DelRecurringCommand(recurringItemId);
		}
	}

	private boolean hasInvalidCommand() {
		return invalidCommand!=null;
	}

	private boolean isInvalidID(){
		return (commandArgumentsString == null ||recurringItemId ==null);
	}
}
