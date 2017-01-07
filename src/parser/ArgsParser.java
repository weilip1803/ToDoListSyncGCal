package parser;
import command.InvalidCommand;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LoggingPermission;

import command.Command;

/**
 *  @@author Josh
 *
 */
public class ArgsParser {
	
	protected static Logger logger = Logger.getLogger("Parser");
	
	protected boolean hasNoArguments=false;
	protected String commandArgumentsString;
	
	public ArgsParser(String commandArguments){
		commandArgumentsString = commandArguments;
		checkForAnyArguments();
	}

	private void checkForAnyArguments() {
		if (commandArgumentsString.equals("")){	
			hasNoArguments=true;
		}
	}
	
	public Command invalidArgs(){
		return new InvalidCommand(commandArgumentsString);
	}
}
