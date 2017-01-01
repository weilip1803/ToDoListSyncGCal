package parser;
import command.InvalidCommand;
import java.util.logging.Logger;



import command.Command;

/**
 *  @@author A0121760R
 *
 */
public class ArgsParser {
	
	protected static Logger logger = Logger.getLogger("Parser");
	
	protected boolean hasNoArguments = false; 
	protected String commandArgumentsString;
	
	public ArgsParser(String commandArguments){
		commandArgumentsString = commandArguments;
		if (isNullArgument()){
			commandArgumentsString = "";
		}
	}

	private boolean isNullArgument() {
		return commandArgumentsString == null;
	}
	
	public Command invalidArgs(){
		return new InvalidCommand(commandArgumentsString);
	}
}
