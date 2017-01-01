package parser;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertNotNull;

import command.Command;
import command.InvalidCommand;
/**
 * @@author A0121760R
 *
 */
public class InvalidParser {
	
	private String invalidCommand;
	protected static Logger logger = Logger.getLogger("Parser");
	private static final String LOG_CREATE_INVALID_PARSER = "InvalidParser Created";
	
	public InvalidParser(String userCommand){
		invalidCommand = userCommand; 	
		assertNotNull(userCommand);
		logger.log(Level.INFO, LOG_CREATE_INVALID_PARSER);
	}
	
	public Command executeCommand(){
		return new InvalidCommand(invalidCommand);
	}
}
