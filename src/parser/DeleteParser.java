package parser;

import command.Command;
import command.DelCommand;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import static org.junit.Assert.assertNotNull;

/**
 *  @@author A0121760R
 *
 */
public class DeleteParser extends ArgsParser{
	
	private static final String LOG_CREATE_DELETE_PARSER = "DeleteParser Created for \"%s\"";
	static final String MESSAGE_INVALID_ID = "The task ID is invalid!";
	
	private Long itemID;
	private Command InvalidCommand = null;
	
	public DeleteParser(String userCommand){
		super(userCommand);
		assertNotNull(commandArgumentsString);
		setItemId();
		logger.log(Level.INFO, String.format(LOG_CREATE_DELETE_PARSER ,
												commandArgumentsString));
	}
	
	/**
	 * This method will return the appropriate Command to be processed
	 * by the Command class.
	 * 
	 * @return DelCommand() if the ID is an long. InvalidCommand if it is not.
	 */
	public Command parse(){
		if (isNullInvalidCommand()){
			itemID = new Long(itemID);
			return new DelCommand(itemID);
		} else{
			return InvalidCommand;
		}
	}
	
	/**
	 * This method attempts to see if the ID is a valid integer or not. If it is,
	 * the itemID field is set. Else, the invalidCommand field will be set.
	 */
	public void setItemId(){
		try{
			//checks if itemID is an long.
			itemID = Long.parseLong(commandArgumentsString);
			
		} catch (Exception e){
			//Set the invalidCommand object
			InvalidParser InvalidArgumentParser = new InvalidParser(MESSAGE_INVALID_ID);
			InvalidCommand = InvalidArgumentParser.executeCommand();
		}
	}
	
	private boolean isNullInvalidCommand() {
		return InvalidCommand == null;
	}
}
