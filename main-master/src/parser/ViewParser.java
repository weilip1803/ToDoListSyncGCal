package parser;

import static org.junit.Assert.assertNotNull;
import java.util.logging.Level;
import command.Command;
import command.ViewCommand;

/**
 *  @@author A0121760R
 *
 */
public class ViewParser extends ArgsParser{
	
	private String view;
	private static final String LOG_CREATE_VIEW_PARSER = "DeleteParser Created for \"%s\"";
	
	public ViewParser(String commandArgument) {
		super(commandArgument); 
		assertNotNull(commandArgumentsString);
		view = commandArgument;
		logger.log(Level.INFO, String.format(LOG_CREATE_VIEW_PARSER ,
				commandArgumentsString));
	}
	
	public Command parse(){
			return new ViewCommand(view);
	}
}